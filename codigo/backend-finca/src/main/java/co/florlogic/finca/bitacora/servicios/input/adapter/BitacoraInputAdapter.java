package co.florlogic.finca.bitacora.servicios.input.adapter;

import java.util.Map;

import org.springframework.stereotype.Service;

import co.florlogic.finca.bitacora.Bitacora;
import co.florlogic.finca.bitacora.servicios.casosuso.LlevarBitacoraCasoUso;

/** Implementa la API pública de la bitácora y solo delega en el caso de uso (A-84). */
@Service
public class BitacoraInputAdapter implements Bitacora {

    private final LlevarBitacoraCasoUso casoUso;

    public BitacoraInputAdapter(LlevarBitacoraCasoUso casoUso) {
        this.casoUso = casoUso;
    }

    @Override
    public void registrar(String actor, String evento, String objeto, Map<String, String> detalle) {
        casoUso.registrar(actor, evento, objeto, detalle);
    }

    @Override
    public boolean cadenaIntegra() {
        return casoUso.cadenaIntegra();
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Service (Spring): registra la clase como bean; Spring la crea una sola vez y le pasa por el constructor lo que pide (inyección por constructor, sin @Autowired). Es el bean que reciben los otros módulos cuando piden la interfaz pública Bitacora.
 * - @Override: le pide al compilador comprobar que el método sí implementa uno de la interfaz; si la firma cambia, no compila.
 */
