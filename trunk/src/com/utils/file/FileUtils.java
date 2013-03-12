/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author manchini
 */
public class FileUtils {

    /**
     * Metodo Que lê um Arquivo e retorna seu conteudo Genérico
     *
     * @param arquivo
     * @return
     * @throws Exception
     */
    public static StringBuilder leArquivo(File arquivo) throws Exception {
        return leArquivoCodificacao(arquivo, "UTF-8");

    }

    /**
     * Metodo Que grava um Arquivo Generico
     *
     * @param conteudo
     * @param destino
     * @throws Exception
     */
    public static void gravaArquivo(CharSequence conteudo, String destino) throws Exception {
        gravaArquivo(conteudo, destino, false);
    }

    /**
     * Metodo Que grava um Arquivo Generico
     *
     * @param conteudo
     * @param destino
     * @param addFinal
     * @return
     * @throws IOException
     */
    public static void gravaArquivo(CharSequence conteudo, String destino, boolean addFinal) throws Exception {
        gravaArquivoCodificacao(conteudo, destino, "UTF-8", addFinal);
    }

    /**
     * Le Arquivo Passando Codificaã§ã£o
     *
     * @param arquivo
     * @param codificacao
     * @return
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static StringBuilder leArquivoCodificacao(File arquivo, String codificacao) throws Exception {
        StringBuilder stringBuilder = new StringBuilder(1024);
        FileInputStream leitor = new FileInputStream(arquivo);
        InputStreamReader in = new InputStreamReader(leitor, codificacao);
        BufferedReader leitorBuf = new BufferedReader(in);
        String line = null;
        while ((line = leitorBuf.readLine()) != null) {
            stringBuilder.append(line).append("\r\n");
        }
        in.close();
        leitor.close();
        return stringBuilder;
    }

    public static StringBuilder leArquivoCodificacao(InputStream arquivo, String codificacao) throws Exception {
        StringBuilder stringBuffer = new StringBuilder(1024);

        InputStreamReader in = new InputStreamReader(arquivo, codificacao);
        BufferedReader leitorBuf = new BufferedReader(in);
        String line = null;
        while ((line = leitorBuf.readLine()) != null) {
            stringBuffer.append(line).append("\r\n");
        }
        in.close();
        return stringBuffer;
    }

    /**
     * Grava Arquivo passando Codificacao
     *
     * @param conteudo
     * @param destino
     * @param codificacao
     * @return
     * @throws Exception
     */
    public static boolean gravaArquivoCodificacao(CharSequence conteudo, String destino, String codificacao, boolean addFinal) throws Exception {
        File file = new File(destino);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream escritor = new FileOutputStream(file, addFinal);
        OutputStreamWriter escritorBuf = new OutputStreamWriter(escritor, codificacao);
        escritorBuf.append(conteudo);
        escritorBuf.close();
        escritor.close();
        return true;
    }

    public static File[] lerM3u(File m3uFile) {
        try {
            String lido = new String(FileUtils.leArquivoCodificacao(m3uFile, "WINDOWS-1252")).replace("\r", "");
            String paths[] = lido.split("\n");
            File[] files = new File[paths.length];
            for (int i = 0; i < paths.length; i++) {
                String path = paths[i];
                if (path.indexOf(":\\") != -1 || path.indexOf('/') == 0) {
                    files[i] = new File(path.replace("\\\\", "/"));
                }
            }
            return files;
        } catch (Exception ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
            return new File[0];
        }
    }
}
