package org.jdesktop.swingworker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public abstract class SwingWorker<T, V> implements Future<T>, Runnable, SwingWorkerSymbols {
   private static final int MAX_WORKER_THREADS = 10;
   private volatile int progress;
   private volatile SwingWorker.StateValue state;
   private final FutureTask<T> future;
   private final PropertyChangeSupport propertyChangeSupport;
   private AccumulativeRunnable<V> doProcess;
   private AccumulativeRunnable<Integer> doNotifyProgressChange;
   private static final AccumulativeRunnable<Runnable> doSubmit = new SwingWorker.DoSubmitAccumulativeRunnable((SwingWorker.DoSubmitAccumulativeRunnable)null);
   private static ExecutorService executorService = null;

   public SwingWorker() {
      Callable<T> callable = new Callable<T>() {
         public T call() throws Exception {
            SwingWorker.this.setState(SwingWorker.StateValue.STARTED);
            return SwingWorker.this.doInBackground();
         }
      };
      this.future = new FutureTask<T>(callable) {
         protected void done() {
            SwingWorker.this.doneEDT();
            SwingWorker.this.setState(SwingWorker.StateValue.DONE);
         }
      };
      this.state = SwingWorker.StateValue.PENDING;
      this.propertyChangeSupport = new SwingWorker.SwingWorkerPropertyChangeSupport(this);
      this.doProcess = null;
      this.doNotifyProgressChange = null;
   }

   protected abstract T doInBackground() throws Exception;

   public final void run() {
      this.future.run();
   }

   @SafeVarargs
   protected final void publish(V... chunks) {
      synchronized(this) {
         if (this.doProcess == null) {
            this.doProcess = new AccumulativeRunnable<V>() {
               public void run(List<V> args) {
                  SwingWorker.this.process(args);
               }

               protected void submit() {
                  SwingWorker.doSubmit.add(this);
               }
            };
         }
      }

      this.doProcess.add(chunks);
   }

   protected void process(List<V> chunks) {
   }

   protected void done() {
   }

   protected final void setProgress(int progress) {
      if (progress >= 0 && progress <= 100) {
         if (this.progress != progress) {
            int oldProgress = this.progress;
            this.progress = progress;
            if (this.getPropertyChangeSupport().hasListeners("progress")) {
               synchronized(this) {
                  if (this.doNotifyProgressChange == null) {
                     this.doNotifyProgressChange = new AccumulativeRunnable<Integer>() {
                        public void run(List<Integer> args) {
                           SwingWorker.this.firePropertyChange("progress", args.get(0), args.get(args.size() - 1));
                        }

                        protected void submit() {
                           SwingWorker.doSubmit.add(this);
                        }
                     };
                  }
               }

               this.doNotifyProgressChange.add(oldProgress, progress);
            }
         }
      } else {
         throw new IllegalArgumentException("the value should be from 0 to 100");
      }
   }

   public final int getProgress() {
      return this.progress;
   }

   public final void execute() {
      getWorkersExecutorService().execute(this);
   }

   public final boolean cancel(boolean mayInterruptIfRunning) {
      return this.future.cancel(mayInterruptIfRunning);
   }

   public final boolean isCancelled() {
      return this.future.isCancelled();
   }

   public final boolean isDone() {
      return this.future.isDone();
   }

   public final T get() throws InterruptedException, ExecutionException {
      return this.future.get();
   }

   public final T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      return this.future.get(timeout, unit);
   }

   public final void addPropertyChangeListener(PropertyChangeListener listener) {
      this.getPropertyChangeSupport().addPropertyChangeListener(listener);
   }

   public final void removePropertyChangeListener(PropertyChangeListener listener) {
      this.getPropertyChangeSupport().removePropertyChangeListener(listener);
   }

   public final void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
      this.getPropertyChangeSupport().firePropertyChange(propertyName, oldValue, newValue);
   }

   public final PropertyChangeSupport getPropertyChangeSupport() {
      return this.propertyChangeSupport;
   }

   public final SwingWorker.StateValue getState() {
      return this.isDone() ? SwingWorker.StateValue.DONE : this.state;
   }

   private void setState(SwingWorker.StateValue state) {
      SwingWorker.StateValue old = this.state;
      this.state = state;
      this.firePropertyChange("state", old, state);
   }

   private void doneEDT() {
      Runnable doDone = new Runnable() {
         public void run() {
            SwingWorker.this.done();
         }
      };
      if (SwingUtilities.isEventDispatchThread()) {
         doDone.run();
      } else {
         doSubmit.add(doDone);
      }

   }

   private static synchronized ExecutorService getWorkersExecutorService() {
      if (executorService == null) {
         ThreadFactory threadFactory = new ThreadFactory() {
            final AtomicInteger threadNumber = new AtomicInteger(1);

            public Thread newThread(Runnable r) {
               StringBuilder name = new StringBuilder("SwingWorker-pool-");
               name.append(System.identityHashCode(this));
               name.append("-thread-");
               name.append(this.threadNumber.getAndIncrement());
               Thread t = new Thread(r, name.toString());
               if (t.isDaemon()) {
                  t.setDaemon(false);
               }

               if (t.getPriority() != 5) {
                  t.setPriority(5);
               }

               return t;
            }
         };
         executorService = new ThreadPoolExecutor(0, 10, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue(), threadFactory) {
            private final ReentrantLock pauseLock = new ReentrantLock();
            private final Condition unpaused;
            private boolean isPaused;
            private final ReentrantLock executeLock;

            {
               this.unpaused = this.pauseLock.newCondition();
               this.isPaused = false;
               this.executeLock = new ReentrantLock();
            }

            public void execute(Runnable command) {
               this.executeLock.lock();

               try {
                  this.pauseLock.lock();

                  try {
                     this.isPaused = true;
                  } finally {
                     this.pauseLock.unlock();
                  }

                  this.setCorePoolSize(10);
                  super.execute(command);
                  this.setCorePoolSize(0);
                  this.pauseLock.lock();

                  try {
                     this.isPaused = false;
                     this.unpaused.signalAll();
                  } finally {
                     this.pauseLock.unlock();
                  }
               } finally {
                  this.executeLock.unlock();
               }

            }

            protected void afterExecute(Runnable r, Throwable t) {
               super.afterExecute(r, t);
               this.pauseLock.lock();

               try {
                  while(this.isPaused) {
                     this.unpaused.await();
                  }
               } catch (InterruptedException var7) {
               } finally {
                  this.pauseLock.unlock();
               }

            }
         };
      }

      return executorService;
   }

   private static class DoSubmitAccumulativeRunnable extends AccumulativeRunnable<Runnable> implements ActionListener {
      private static final int DELAY = 33;

      private DoSubmitAccumulativeRunnable() {
      }

      protected void run(List<Runnable> args) {
         int i = 0;

         try {
            Iterator var4 = args.iterator();

            while(var4.hasNext()) {
               Runnable runnable = (Runnable)var4.next();
               ++i;
               runnable.run();
            }
         } finally {
            if (i < args.size()) {
               Runnable[] argsTail = new Runnable[args.size() - i];

               for(int j = 0; j < argsTail.length; ++j) {
                  argsTail[j] = (Runnable)args.get(i + j);
               }

               this.add(true, argsTail);
            }

         }

      }

      protected void submit() {
         Timer timer = new Timer(33, this);
         timer.setRepeats(false);
         timer.start();
      }

      public void actionPerformed(ActionEvent event) {
         this.run();
      }

      // $FF: synthetic method
      DoSubmitAccumulativeRunnable(SwingWorker.DoSubmitAccumulativeRunnable var1) {
         this();
      }
   }

   public static enum StateValue {
      PENDING,
      STARTED,
      DONE;
   }

   private class SwingWorkerPropertyChangeSupport extends PropertyChangeSupport {
      private static final long serialVersionUID = -6694014073227886163L;

      SwingWorkerPropertyChangeSupport(Object source) {
         super(source);
      }

      public void firePropertyChange(final PropertyChangeEvent evt) {
         if (SwingUtilities.isEventDispatchThread()) {
            super.firePropertyChange(evt);
         } else {
            SwingWorker.doSubmit.add(new Runnable() {
               public void run() {
                  SwingWorkerPropertyChangeSupport.this.firePropertyChange(evt);
               }
            });
         }

      }
   }
}
