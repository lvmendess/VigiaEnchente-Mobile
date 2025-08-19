package com.example.vigiaenchente_mobile;

public class ClimaResponse {
    private Main main;
    private String cidade;

    public Main getMain(){return main;}
    public String getCidade(){return cidade;}

    public class Main{
        private float temp;
        public float getTemp(){return temp;}
    }
}
