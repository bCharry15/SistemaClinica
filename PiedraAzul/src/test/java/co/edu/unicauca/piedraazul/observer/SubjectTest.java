package co.edu.unicauca.piedraazul.observer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase Subject (patron Observer).
 */
class SubjectTest {

    // Implementacion concreta de Subject para pruebas
    static class ConcreteSubject extends Subject {
        public void triggerNotification(String msg) {
            notifyObservers(msg);
        }
    }

    // Implementacion concreta de Observer que registra mensajes
    static class RecordingObserver implements Observer {
        private final List<String> receivedMessages = new ArrayList<>();

        @Override
        public void update(String message) {
            receivedMessages.add(message);
        }

        public List<String> getReceivedMessages() {
            return receivedMessages;
        }
    }

    private ConcreteSubject subject;
    private RecordingObserver observerA;
    private RecordingObserver observerB;

    @BeforeEach
    void setUp() {
        subject = new ConcreteSubject();
        observerA = new RecordingObserver();
        observerB = new RecordingObserver();
    }

    // Prueba 1: attach agrega un observer y recibe notificaciones
    @Test
    void test01_attachObserverReceivesNotification() {
        subject.attach(observerA);
        subject.triggerNotification("hola");

        assertEquals(1, observerA.getReceivedMessages().size());
        assertEquals("hola", observerA.getReceivedMessages().get(0));
    }

    // Prueba 2: detach elimina el observer y ya no recibe notificaciones
    @Test
    void test02_detachObserverStopsReceivingNotifications() {
        subject.attach(observerA);
        subject.detach(observerA);
        subject.triggerNotification("adios");

        assertTrue(observerA.getReceivedMessages().isEmpty());
    }

    // Prueba 3: multiples observers reciben la misma notificacion
    @Test
    void test03_multipleObserversAllReceiveNotification() {
        subject.attach(observerA);
        subject.attach(observerB);
        subject.triggerNotification("broadcast");

        assertEquals("broadcast", observerA.getReceivedMessages().get(0));
        assertEquals("broadcast", observerB.getReceivedMessages().get(0));
    }

    // Prueba 4: sin observers, notifyObservers no lanza excepcion
    @Test
    void test04_noObserversNoException() {
        assertDoesNotThrow(() -> subject.triggerNotification("nadie escucha"));
    }

    // Prueba 5: multiples notificaciones se acumulan en orden
    @Test
    void test05_multipleNotificationsInOrder() {
        subject.attach(observerA);
        subject.triggerNotification("primero");
        subject.triggerNotification("segundo");
        subject.triggerNotification("tercero");

        List<String> msgs = observerA.getReceivedMessages();
        assertEquals(3, msgs.size());
        assertEquals("primero", msgs.get(0));
        assertEquals("segundo", msgs.get(1));
        assertEquals("tercero", msgs.get(2));
    }

    // Prueba 6: detach de un observer que no fue adjuntado no lanza excepcion
    @Test
    void test06_detachNonAttachedObserverNoException() {
        assertDoesNotThrow(() -> subject.detach(observerA));
    }
}
