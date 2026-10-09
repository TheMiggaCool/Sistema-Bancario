package ar.edu.unju.fi.arquitecturas.tp2daas.listener;

import ar.edu.unju.fi.arquitecturas.tp2daas.events.ClienteCreadoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ClienteNotificacionListener {

    @Async
    @EventListener
    public void handleClienteCreado(ClienteCreadoEvent event) {
        String urlActivacion = "http://localhost:8080/api/v1/clientes/activar?token=" + event.getToken();

        String htmlBody = """
            <html>
                <body>
                    <h2>¡Bienvenido al Core Bancario, %s!</h2>
                    <p>Tu cuenta ha sido creada exitosamente. Para activarla, haz clic en el siguiente enlace:</p>
                    <a href="%s" style="background-color: #28a745; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">Activar Cuenta</a>
                    <p>Este enlace expirará en 24 horas.</p>
                </body>
            </html>
            """.formatted(event.getNombre(), urlActivacion);

        log.info("--- [CORREO ASÍNCRONO ENVIADO A: {}] ---", event.getEmail());
        log.info("Cuerpo HTML generado:\n{}", htmlBody);
    }
}