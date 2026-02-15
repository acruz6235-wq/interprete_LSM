package com.example.interpretels.database;

import android.content.Context;
import com.example.interpretels.models.Sign;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSeeder {

    public static void seedDatabase(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        SignDao dao = db.signDao();

        // Verificar si ya hay datos
        if (dao.getSignCount() > 0) {
            return;
        }

        List<Sign> signs = new ArrayList<>();

        // Abecedario (A-Z)
        signs.add(new Sign("A", "Letra A en lenguaje de señas", "sign_a", "alphabet"));
        signs.add(new Sign("B", "Letra B en lenguaje de señas", "sign_b", "alphabet"));
        signs.add(new Sign("C", "Letra C en lenguaje de señas", "sign_c", "alphabet"));
        signs.add(new Sign("D", "Letra D en lenguaje de señas", "sign_d", "alphabet"));
        signs.add(new Sign("E", "Letra E en lenguaje de señas", "sign_e", "alphabet"));
        signs.add(new Sign("F", "Letra F en lenguaje de señas", "sign_f", "alphabet"));
        signs.add(new Sign("G", "Letra G en lenguaje de señas", "sign_g", "alphabet"));
        signs.add(new Sign("H", "Letra H en lenguaje de señas", "sign_h", "alphabet"));
        signs.add(new Sign("I", "Letra I en lenguaje de señas", "sign_i", "alphabet"));
        signs.add(new Sign("J", "Letra J en lenguaje de señas", "sign_j", "alphabet"));
        signs.add(new Sign("K", "Letra K en lenguaje de señas", "sign_k", "alphabet"));
        signs.add(new Sign("L", "Letra L en lenguaje de señas", "sign_l", "alphabet"));
        signs.add(new Sign("M", "Letra M en lenguaje de señas", "sign_m", "alphabet"));
        signs.add(new Sign("N", "Letra N en lenguaje de señas", "sign_n", "alphabet"));
        signs.add(new Sign("O", "Letra O en lenguaje de señas", "sign_o", "alphabet"));
        signs.add(new Sign("P", "Letra P en lenguaje de señas", "sign_p", "alphabet"));
        signs.add(new Sign("Q", "Letra Q en lenguaje de señas", "sign_q", "alphabet"));
        signs.add(new Sign("R", "Letra R en lenguaje de señas", "sign_r", "alphabet"));
        signs.add(new Sign("S", "Letra S en lenguaje de señas", "sign_s", "alphabet"));
        signs.add(new Sign("T", "Letra T en lenguaje de señas", "sign_t", "alphabet"));
        signs.add(new Sign("U", "Letra U en lenguaje de señas", "sign_u", "alphabet"));
        signs.add(new Sign("V", "Letra V en lenguaje de señas", "sign_v", "alphabet"));
        signs.add(new Sign("W", "Letra W en lenguaje de señas", "sign_w", "alphabet"));
        signs.add(new Sign("X", "Letra X en lenguaje de señas", "sign_x", "alphabet"));
        signs.add(new Sign("Y", "Letra Y en lenguaje de señas", "sign_y", "alphabet"));
        signs.add(new Sign("Z", "Letra Z en lenguaje de señas", "sign_z", "alphabet"));

        // Señales Básicas
        signs.add(new Sign("Hola", "Saludo en lenguaje de señas", "sign_hola", "basic"));
        signs.add(new Sign("Gracias", "Expresar gratitud", "sign_gracias", "basic"));
        signs.add(new Sign("Sí", "Afirmación", "sign_si", "basic"));
        signs.add(new Sign("No", "Negación", "sign_no", "basic"));
        signs.add(new Sign("Por favor", "Solicitud cortés", "sign_porfavor", "basic"));
        signs.add(new Sign("Adiós", "Despedida", "sign_adios", "basic"));

        // Señales para Aprender
        signs.add(new Sign("Familia", "Concepto de familia", "sign_familia", "learn"));
        signs.add(new Sign("Amigo", "Concepto de amistad", "sign_amigo", "learn"));
        signs.add(new Sign("Casa", "Lugar de residencia", "sign_casa", "learn"));
        signs.add(new Sign("Comida", "Alimento", "sign_comida", "learn"));
        signs.add(new Sign("Agua", "Líquido vital", "sign_agua", "learn"));

        // Insertar todas
        dao.insertAll(signs);
    }
}