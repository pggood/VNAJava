
@echo off                                    
rem (c) DL2SBA 2018                          
if not exist vnaJ.3.4.8.jar goto err1            
                                             
java -Duser.home=./ -Duser.language=en -Duser.region=US -jar vnaJ.3.4.8.jar
pause
goto end                                     
                                             
:err1                                        
echo !!! ------------------------------------
echo !!! program file vnaJ.3.4.8.jar missing     
echo !!! aborting                            
pause                                        
goto end                                     
                                             
:end                                         
