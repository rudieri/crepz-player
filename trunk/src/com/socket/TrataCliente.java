package com.socket;


import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

class TrataCliente extends Thread {

    /**
     * @return the envio
     */
    public static byte[] getEnvio() {
        return envio;
    }

    /**
     * @param aEnvio the envio to set
     */
    public static void setEnvio(byte[] aEnvio) {
        envio = aEnvio;
    }

    private Socket cliente;
    private Servidor servidor;

    private static byte [] envio;

    public TrataCliente(Servidor servidor) throws IOException {
        this.servidor = servidor;
        cliente = servidor.getAccept();
        start();
    }

    public void run() {
        try {
            OutputStream out = cliente.getOutputStream();
            while(servidor.isOn()){
                out.write(getEnvio());
            }
            out.close();
            cliente.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    
}
