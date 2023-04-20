/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dekker5;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Desarrollo
 */
public class Dekker5 {

    private MainFrame frame;
    private volatile boolean p1_puede_entrar, p2_puede_entrar, cancelar;
    private volatile int turno;
    private static final long TIME_ALEATORIO = 630;
    private static final long TIME1 = 1500;
    private static final long TIME2 = 1700;
    
    public Dekker5(MainFrame frame) {
        this.frame = frame;
    }

    private void retardar_unos_milisegundos(long tiempo) throws InterruptedException {
        Thread.sleep(tiempo);
    }
    
    private void retardo_aleatorio() throws InterruptedException {       
        retardar_unos_milisegundos(TIME_ALEATORIO);
    }
    
    private void seccion_critica1() throws InterruptedException {
        retardar_unos_milisegundos(TIME2);
    }
    
    private void seccion_critica2() throws InterruptedException {
        retardar_unos_milisegundos(TIME1);
    }

    private void setStatus1(String status) {
        frame.setStatus1(status);
    }
    
    private void setStatus2(String status) {
        frame.setStatus2(status);
    }
    
    private void setWork1 () {
        frame.setImageWork1();
    }
    
    private void setWork2 () {
        frame.setImageWork2();
    } 
    
    private void setWait1 () {
        frame.setImageWait1();
    }
    
    private void setWait2 () {
        frame.setImageWait2();
    } 
    
    private void cleanWork1() {
        frame.cleanImage1();
    }
    
    private void cleanWork2() {
        frame.cleanImage2();
    }
    
    private void Proceso1() throws InterruptedException {
        while (!cancelar) {            
            setWork1();setStatus1("Tareas Iniciales");retardo_aleatorio();//[REALIZA_TAREAS_INICIALES]
            p1_puede_entrar = true;
            while (p2_puede_entrar && !cancelar) {                
                if (turno == 2) {
                    p1_puede_entrar = false;
                    setStatus1("Esperando recursos");
                    setWait1();
                    while (turno == 2 && !cancelar) {
                    }
                    p1_puede_entrar = true;
                }
            }   
            if(cancelar) break;
            setWork1();setStatus1("Sección Critica 1");seccion_critica1();//[REGION_CRITICA]
            turno = 2;
            p1_puede_entrar = false;
            setStatus1("Tareas Finales");retardo_aleatorio();//[REALIZA_TAREAS_FINALES]
        }
        setStatus1("Proceso Terminado");
        cleanWork1();
    }

    private void Proceso2() throws InterruptedException {
        while (!cancelar) {
            setWork2();setStatus2("Tareas Iniciales");retardo_aleatorio();//[REALIZA_TAREAS_INICIALES]
            p2_puede_entrar = true;
            while (p1_puede_entrar && !cancelar) {
                if (turno == 1) {
                    p2_puede_entrar = false;
                    setStatus2("Esperando recursos");
                    setWait2();
                    while (turno == 1 && !cancelar) {
                    }
                    p2_puede_entrar = true;
                }
            }
            
            if(cancelar) break;
            //[REGION_CRITICA]
            setWork2();setStatus2("Sección Critica 2");seccion_critica2();
            turno = 1;
            p2_puede_entrar = false;
            setStatus2("Tareas Finales");retardo_aleatorio();//[REALIZA_TAREAS_FINALES]
        }
        setStatus2("Proceso Terminado");
        cleanWork2();
    }

    public void cancelar() {
        synchronized (this) {
            cancelar = true;
        }
    }
    
    public void iniciar() {
        p1_puede_entrar = false;
        p2_puede_entrar = false;
        cancelar = false;
        turno = 1;
        Thread proceso1 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Proceso1();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Dekker5.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        });
        
        Thread proceso2 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Proceso2();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Dekker5.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        });
                
        proceso1.start();
        proceso2.start();
        
        try {
            proceso1.join();
            proceso2.join();            
        } catch (InterruptedException ex) {
            Logger.getLogger(Dekker5.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

}
