package Estructura;

import java.util.Arrays;

public class Lista {

    class Nodo {

        int[] info;
        Nodo ant, sig;
    }
    private Nodo raiz;

    public Lista() {
        raiz = null;
    }

    public void insertar(int[] x) {
        Nodo nuevo = new Nodo();
        nuevo.info = depurar(x);
        if (nuevo.info.length != 1) {
            if (cantidad() == 0) {
                raiz = nuevo;
            } else if (!checaNumero(x)) {
                Nodo nodo = new Nodo();
                nodo.info = depurar(x);
                nodo.sig = raiz;
                raiz = nodo;
            } else {
                insertaEspecial(x);
            }
        }
    }

    public int buscar(int x) {
        Nodo nodo = raiz;
        while (nodo != null) {
            for (int i = 0; i < nodo.info.length; i++) {
                if (x == nodo.info[i]) {
                    return nodo.info[0];
                }
            }
            nodo = nodo.sig;
        }
        return x;
    }

    public int cantidad() {
        int cant = 0;
        Nodo reco = raiz;
        while (reco != null) {
            reco = reco.sig;
            cant++;
        }
        return cant;
    }

    public boolean vacia() {
        return raiz == null;
    }

    public void imprime() {
        if (vacia()) {
            System.out.println("está vacia");
        } else {
            System.out.println("-----------------------------");
            Nodo aux = raiz;
            while (aux != null) {
                System.out.println(Arrays.toString(aux.info));
                aux = aux.sig;
            }
        }
        System.out.println("-----------------------------");
    }

    public boolean checaNumero(int[] x) {
        Nodo nodo = raiz;
        boolean bandera = false;
        while (nodo != null) {
            if (verificaEnNodo(nodo, x)) {
                bandera = true;
            }
            nodo = nodo.sig;
        }
        return bandera;
    }

    private boolean verificaEnNodo(Nodo nodo, int[] ar) {
        int[] x = nodo.info;
        for (int i = 0; i < ar.length; i++) {
            for (int j = 0; j < x.length; j++) {
                if (ar[i] == x[j]) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean verificaEnNodoNum(Nodo nodo, int ar) {
        int[] x = nodo.info;
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x.length; j++) {
                if (x[i] == ar) {
                    return true;
                }
            }
        }
        return false;
    }

    private int[] depurar(int[] x) {
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x.length; j++) {
                x[j] = (i != j && x[i] == x[j]) ? 0 : x[j];
            }
        }
        int cont = 0;
        for (int i = 0; i < x.length; i++) {
            cont += (x[i] == 0) ? 1 : 0;
        }
        int contador = 0;
        int[] ar = new int[x.length - cont];
        for (int i = 0; i < x.length; i++) {
            if (x[i] != 0) {
                ar[contador] = x[i];
                contador++;
            }
        }

        return ar;
    }

    private void insertaEspecial(int[] x) {
        Nodo nodo = raiz;
        
        while (nodo != null) {
            if (verificaEnNodo(nodo, x)) {
                int cont = nodo.info.length;
                int[] arreglo = new int[cont+x.length];
                for(int i=0;i<cont;i++){
                    arreglo[i] = nodo.info[i];
                }
                for (int i = 0; i < x.length; i++) {
                    if (!verificaEnNodoNum(nodo, x[i])) {
                        arreglo[i+nodo.info.length] = x[i];
                        cont++;
                    }
                }
                arreglo = depurar(arreglo);
                nodo.info = arreglo;
            }
            nodo = nodo.sig;
        }
    }

    public static void main(String[] args) {
        Lista l = new Lista();
        int[] a1 = {1, 6};//---
        int[] a2 = {2, 3};//---
        int[] a3 = {4, 3};//
        int[] a4 = {5, 4};//---
        int[] a5 = {5, 2};//
        int[] a6 = {5, 7};//
        int[] a7 = {7, 2};//
        l.insertar(a1);
        l.insertar(a2);
        l.insertar(a3);
        l.insertar(a4);
        l.insertar(a5);
        l.insertar(a6);
        l.insertar(a7);
        l.imprime();
    }

}
