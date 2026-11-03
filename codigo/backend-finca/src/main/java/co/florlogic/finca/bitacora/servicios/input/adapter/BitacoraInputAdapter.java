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
