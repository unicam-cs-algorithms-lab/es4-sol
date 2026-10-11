package it.unicam.cs.asdl.es4sol;

import static org.junit.jupiter.api.Assertions.*;
import java.util.GregorianCalendar;
import org.junit.jupiter.api.Test;

/**
 * Test aggiuntivi: invarianti degli array, polimorfismo, identità logica
 * e copie difensive. Ogni test documenta una proprietà osservabile dell'API.
 */
public class ContrattiDidatticiTest {

    private static TimeSlot slot(int startMinute, int endMinute) {
        // Usiamo istanti nello stesso giorno per isolare la logica temporale.
        GregorianCalendar start = new GregorianCalendar(2026, 9, 15, 9, startMinute);
        GregorianCalendar stop = new GregorianCalendar(2026, 9, 15, 9, endMinute);
        start.set(GregorianCalendar.SECOND, 0);
        start.set(GregorianCalendar.MILLISECOND, 0);
        stop.set(GregorianCalendar.SECOND, 0);
        stop.set(GregorianCalendar.MILLISECOND, 0);
        return new TimeSlot(start, stop);
    }

    @Test
    public void testIdentitaFacilityNonCoincideConSoddisfacimento() {
        // La superclasse definisce l'identità per codice; le sottoclassi
        // definiscono una relazione di soddisfacimento diversa.
        Facility presenza = new PresenceFacility("PC", "Computer");
        Facility quantita = new QuantitativeFacility("PC", "Postazioni", 20);
        assertEquals(presenza, quantita);
        assertEquals(presenza.hashCode(), quantita.hashCode());
        assertFalse(presenza.satisfies(quantita));
        assertFalse(quantita.satisfies(presenza));
    }

    @Test
    public void testQuantitaSoddisfaRichiestaNonSuperiore() {
        // Il metodo invocato attraverso Facility è quello della sottoclasse concreta.
        Facility disponibile = new QuantitativeFacility("SED", "Sedute", 30);
        assertTrue(disponibile.satisfies(new QuantitativeFacility("SED", "Sedute", 30)));
        assertTrue(disponibile.satisfies(new QuantitativeFacility("SED", "Sedute", 20)));
        assertFalse(disponibile.satisfies(new QuantitativeFacility("SED", "Sedute", 31)));
        assertFalse(disponibile.satisfies(new QuantitativeFacility("PC", "Computer", 10)));
        assertThrows(NullPointerException.class, () -> disponibile.satisfies(null));
    }

    @Test
    public void testAulaRaddoppiaCapacitaSenzaContareDuplicati() {
        // Distinzione fra dimensione logica e capacità fisica dell'array.
        Aula a = new Aula("A", "Polo");
        assertEquals(0, a.getNumeroFacilities());
        assertEquals(5, a.getFacilities().length);
        for (int i = 0; i < 6; i++)
            assertTrue(a.addFacility(new PresenceFacility("F" + i, "Servizio")));
        assertEquals(6, a.getNumeroFacilities());
        assertEquals(10, a.getFacilities().length);
        assertFalse(a.addFacility(new QuantitativeFacility("F0", "Altra", 9)));
        assertEquals(6, a.getNumeroFacilities());
        assertThrows(NullPointerException.class, () -> a.addFacility(null));
    }

    @Test
    public void testRichiesteNullEPolimorfismo() {
        // Le posizioni null sono ignorate; ogni richiesta non nulla
        // deve essere soddisfatta da almeno una facility concreta.
        Aula a = new Aula("A", "Polo");
        a.addFacility(new PresenceFacility("VID", "Proiettore"));
        a.addFacility(new QuantitativeFacility("SED", "Sedute", 50));
        assertTrue(a.satisfiesFacilities(new Facility[] {null,
                new PresenceFacility("VID", "Video"),
                new QuantitativeFacility("SED", "Sedute", 30)}));
        assertFalse(a.satisfiesFacilities(new Facility[] {
                new QuantitativeFacility("SED", "Sedute", 51)}));
        assertTrue(a.satisfiesFacilities(new Facility[] {null, null}));
        assertThrows(NullPointerException.class, () -> a.satisfiesFacilities(null));
    }

    @Test
    public void testSovrapposizioneEStatoDopoRifiuto() {
        // Un tentativo fallito non deve incrementare il numero di prenotazioni.
        Aula a = new Aula("A", "Polo");
        a.addPrenotazione(slot(0, 30), "Docente", "Lezione");
        assertTrue(a.isFree(slot(30, 45))); // intervalli a contatto
        assertTrue(a.isFree(slot(25, 45))); // esattamente 5 minuti
        assertFalse(a.isFree(slot(24, 45))); // 6 minuti
        assertThrows(IllegalArgumentException.class,
                () -> a.addPrenotazione(slot(24, 45), "Altro", "Seminario"));
        assertEquals(1, a.getNumeroPrenotazioni());
        assertThrows(NullPointerException.class,
                () -> a.addPrenotazione(null, "Docente", "Motivo"));
        assertEquals(1, a.getNumeroPrenotazioni());
    }

    @Test
    public void testPrenotazioneIdentitaStabileDopoSetter() {
        // Docente e motivo sono mutabili ma non definiscono l'identità logica.
        Aula a = new Aula("A", "Polo");
        TimeSlot ts = slot(0, 30);
        Prenotazione p = new Prenotazione(a, ts, "Uno", "Lezione");
        Prenotazione q = new Prenotazione(new Aula("A", "Altro"), ts,
                "Due", "Esame");
        int hash = p.hashCode();
        p.setDocente("Nuovo");
        p.setMotivo("Altro");
        assertEquals(p, q);
        assertEquals(0, p.compareTo(q));
        assertEquals(hash, p.hashCode());
    }

    @Test
    public void testTimeSlotCopieDifensive() {
        // Modificare gli argomenti originali e i valori restituiti dai getter
        // non deve modificare un intervallo già costruito.
        GregorianCalendar start = new GregorianCalendar(2026, 9, 15, 9, 0);
        GregorianCalendar stop = new GregorianCalendar(2026, 9, 15, 10, 0);
        TimeSlot ts = new TimeSlot(start, stop);
        long originalStart = ts.getStart().getTimeInMillis();
        long originalStop = ts.getStop().getTimeInMillis();
        start.add(GregorianCalendar.DAY_OF_MONTH, 3);
        stop.add(GregorianCalendar.DAY_OF_MONTH, 3);
        ts.getStart().add(GregorianCalendar.DAY_OF_MONTH, 5);
        ts.getStop().add(GregorianCalendar.DAY_OF_MONTH, 5);
        assertEquals(originalStart, ts.getStart().getTimeInMillis());
        assertEquals(originalStop, ts.getStop().getTimeInMillis());
    }

    @Test
    public void testMinutiSovrapposizioneSoglia() {
        // Contatto: -1; 5 minuti: non sovrapposto; 6 minuti: sovrapposto.
        TimeSlot base = slot(0, 30);
        assertEquals(-1, base.getMinutesOfOverlappingWith(slot(30, 45)));
        assertEquals(5, base.getMinutesOfOverlappingWith(slot(25, 45)));
        assertFalse(base.overlapsWith(slot(25, 45)));
        assertTrue(base.overlapsWith(slot(24, 45)));
        assertEquals(base.getMinutesOfOverlappingWith(slot(24, 45)),
                slot(24, 45).getMinutesOfOverlappingWith(base));
    }
}
