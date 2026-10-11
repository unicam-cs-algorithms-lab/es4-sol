package it.unicam.cs.asdl.es4sol;

/**
 * Modella un'aula identificata univocamente dal nome, dotata di servizi e prenotazioni.
     * <p>La classe mostra come gestire collezioni mediante array a capacità variabile:
     * {@code numFacilities} e {@code numPrenotazioni} indicano la parte occupata,
     * mentre {@code array.length} rappresenta la capacità allocata. L'inserimento
     * deve evitare duplicati e ampliare l'array quando è pieno.
     * <p>Il tipo {@link Facility} consente di memorizzare oggetti di sottoclassi
     * differenti nello stesso array e invocare polimorficamente {@code satisfies}.
 */
public class Aula implements Comparable<Aula> {

    /*
     * numero iniziale delle posizioni dell'array facilities. Se viene richiesto
     * di inserire una facility e l'array è pieno questo viene raddoppiato. La
     * costante è protected solo per consentirne l'accesso ai test JUnit
     */
    /** Capacità iniziale dell'array delle facility; distinta dalla dimensione logica. */
    protected static final int INIT_NUM_FACILITIES = 5;

    /*
     * numero iniziale delle posizioni dell'array prenotazioni. Se viene
     * richiesto di inserire una prenotazione e l'array è pieno questo viene
     * raddoppiato. La costante è protected solo per consentirne l'accesso ai
     * test JUnit.
     */
    /** Capacità iniziale dell'array delle prenotazioni; distinta dalla dimensione logica. */
    protected static final int INIT_NUM_PRENOTAZIONI = 100;

    // Identificativo unico di un'aula
    private final String nome;

    // Location dell'aula
    private final String location;

    /*
     * Insieme delle facilities di quest'aula. L'array viene creato all'inizio
     * della dimensione specificata nella costante INIT_NUM_FACILITIES. Il
     * metodo addFacility(Facility) raddoppia l'array qualora non ci sia più
     * spazio per inserire la facility.
     */
    private Facility[] facilities;

    // Invariante: le prime numFacilities posizioni sono occupate.
    // numero corrente di facilities inserite
    private int numFacilities;

    /*
     * Insieme delle prenotazioni per quest'aula. L'array viene creato
     * all'inizio della dimensione specificata nella costante
     * INIT_NUM_PRENOTAZIONI. Il metodo addPrenotazione(TimeSlot, String,
     * String) raddoppia l'array qualora non ci sia più spazio per inserire la
     * prenotazione.
     */
    private Prenotazione[] prenotazioni;

    // Invariante: le prime numPrenotazioni posizioni sono occupate.
    // numero corrente di prenotazioni inserite
    private int numPrenotazioni;

    /**
     * Costruisce un'aula senza servizi e senza prenotazioni.
     * Inizializza due array con capacità rispettivamente 5 e 100, e contatori a zero.
     * @param nome identificativo logico dell'aula
     * @param location collocazione dell'aula
     * @throws NullPointerException se nome o location sono null
     */
    public Aula(String nome, String location) {
        if (nome == null)
            throw new NullPointerException(
                    "Creazione di un'aula con nome null");
        if (location == null)
            throw new NullPointerException(
                    "Creazione di un'aula con location null");
        this.nome = nome;
        this.location = location;
        this.facilities = new Facility[INIT_NUM_FACILITIES];
        this.numFacilities = 0;
        this.prenotazioni = new Prenotazione[INIT_NUM_PRENOTAZIONI];
        this.numPrenotazioni = 0;
    }

    /**
     * Calcola l'hash usando esclusivamente il nome, come in {@code equals}.
     * @return codice hash dell'identificativo dell'aula
     */
    @Override
    public int hashCode() {
        return this.nome.hashCode();
    }

    /**
     * Confronta l'identità logica: due aule sono uguali se hanno lo stesso nome.
     * Location, servizi e prenotazioni non partecipano al confronto.
     * @param obj oggetto da confrontare
     * @return true se obj è un'aula con lo stesso nome
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (obj instanceof Aula) {
            Aula other = (Aula) obj;
            return this.nome.equals(other.nome);
        }
        return false;
    }

    /**
     * Ordina le aule lessicograficamente per nome, coerentemente con equals.
     * @param o altra aula
     * @return negativo, zero o positivo secondo l'ordine dei nomi
     * @throws NullPointerException se o è null
     */
    @Override
    public int compareTo(Aula o) {
        return this.nome.compareTo(o.nome);
    }

    /**
     * Restituisce l'array delle facility, inclusa la capacità inutilizzata.
     * <p><b>Attenzione didattica:</b> viene esposto direttamente l'array interno:
     * modificarlo dall'esterno può compromettere gli invarianti della classe.
     * È una scelta del contratto originale, non un modello di incapsulamento.
     * @return array interno; sono occupate solo le prime {@code getNumeroFacilities()} posizioni
     */
    public Facility[] getFacilities() {
        return this.facilities;
    }

    /**
     * Restituisce il numero di facility effettivamente memorizzate.
     * @return numero di posizioni occupate, non capacità dell'array
     */
    public int getNumeroFacilities() {
        return this.numFacilities;
    }

    /**
     * Restituisce l'identificativo logico dell'aula.
     * @return nome dell'aula
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Restituisce la collocazione dell'aula.
     * @return location
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * Restituisce l'array interno delle prenotazioni, compresa la capacità libera.
     * <p>Come {@code getFacilities()}, espone un riferimento mutabile interno:
     * gli utenti dell'API non devono alterarne gli elementi.
     * @return array interno con le prime {@code getNumeroPrenotazioni()} posizioni occupate
     */
    public Prenotazione[] getPrenotazioni() {
        return this.prenotazioni;
    }

    /**
     * Restituisce il numero di prenotazioni registrate.
     * @return numero di posizioni occupate
     */
    public int getNumeroPrenotazioni() {
        return this.numPrenotazioni;
    }

    /**
     * Aggiunge una facility soltanto se non è già presente una facility uguale.
     * <p>La ricerca usa {@code equals}, non {@code ==}; l'array viene raddoppiato
     * quando il numero di elementi coincide con la capacità.
     * @param f facility da aggiungere
     * @return true se inserita, false se già presente
     * @throws NullPointerException se f è null
     */
    public boolean addFacility(Facility f) {
        /*
         * Nota: attenzione! Per controllare se una facility è già presente
         * bisogna usare il metodo equals della classe Facility.
     * Nota: attenzione bis! Si noti che per le sottoclassi di Facility non
         * è richiesto di ridefinire ulteriormente il metodo equals...
         */
        if (f == null)
            throw new NullPointerException("Aggiunta di una facility null");
        // Ricerca lineare sulle sole posizioni occupate: O(n).
        // Cerco se la facility è già presente
        for (int i = 0; i < this.numFacilities; i++)
            if (this.facilities[i].equals(f))
                return false; // la facility era già presente e non la aggiungo
        // la facility non è presente
        // controllo se c'è spazio
        if (this.numFacilities < this.facilities.length) {
            // c'è spazio e la inserisco
            this.facilities[this.numFacilities] = f;
            this.numFacilities++;
        } else {
            // Il raddoppio copia i riferimenti, non gli oggetti.
            // non c'è spazio, raddoppio l'array
            this.facilities = (Facility[]) raddoppia(this.facilities);
            // ora c'è spazio e inserisco
            this.facilities[this.numFacilities] = f;
            this.numFacilities++;
        }
        return true;
    }

    /**
     * Verifica se l'aula non ha prenotazioni sovrapposte all'intervallo dato.
     * <p>Si usa la soglia definita da {@link TimeSlot#overlapsWith(TimeSlot)}.
     * @param ts intervallo richiesto
     * @return true se nessuna prenotazione supera la soglia di sovrapposizione
     * @throws NullPointerException se ts è null
     */
    public boolean isFree(TimeSlot ts) {
        if (ts == null)
            throw new NullPointerException("Il timeslot passato è nullo");
        // cerco se c'è sovrapposizione con almeno una prenotazione
        for (int i = 0; i < numPrenotazioni; i++) {
            if (prenotazioni[i].getTimeSlot().overlapsWith(ts))
                return false;
        }
        // non c'è nessuna sovrapposizione
        return true;
    }

    /**
     * Verifica se ogni servizio richiesto è soddisfatto da almeno un servizio dell'aula.
     * <p>Le posizioni null dell'array di richieste sono ignorate. Il metodo
     * invoca {@code satisfies} sulle istanze effettive: esempio di dispatch dinamico.
     * @param requestedFacilities richieste; possono contenere posizioni null
     * @return true se tutte le richieste non nulle sono soddisfatte
     * @throws NullPointerException se l'array è null
     */
    public boolean satisfiesFacilities(Facility[] requestedFacilities) {
        if (requestedFacilities == null)
            throw new NullPointerException(
                    "Il set di facility richieste è nullo");
        // Dispatch dinamico: satisfies dipende dalla sottoclasse concreta.
        // provo a soddisfare tutte le facilities richieste
        for (int i = 0; i < requestedFacilities.length; i++) {
            // ignoro eventuali posizioni null
            if (requestedFacilities[i] == null)
                continue;
            // scorro le facilities di questa aula e vedo se almeno una soddisfa
            // la facility richiesta i
            boolean trovata = false;
            int j = 0;
            while (j < this.numFacilities && !trovata)
                if (this.facilities[j].satisfies(requestedFacilities[i]))
                    trovata = true;
                else
                    j++;
            // vedo se non è stata trovata nessuna facility che soddisfa la
            // facility i
            if (!trovata)
                return false;
        }
        return true;
    }

    /**
     * Registra una prenotazione solo se non si sovrappone a quelle esistenti.
     * <p>Se necessario raddoppia l'array; in caso di rifiuto non deve aumentare
     * il contatore né inserire una prenotazione parziale.
     * @param ts intervallo da prenotare
     * @param docente nome del docente
     * @param motivo motivo della prenotazione
     * @throws NullPointerException se uno degli argomenti è null
     * @throws IllegalArgumentException se l'intervallo si sovrappone a una prenotazione
     */
    public void addPrenotazione(TimeSlot ts, String docente, String motivo) {
        if (ts == null)
            throw new NullPointerException(
                    "TimeSlot della nuova prenotazione null");
        if (docente == null)
            throw new NullPointerException(
                    "Docente della nuova prenotazione null");
        if (motivo == null)
            throw new NullPointerException(
                    "Motivo della nuova prenotazione null");
        // Validiamo prima di mutare lo stato interno.
        // Cerco se la prenotazione può essere effettuata
        for (int i = 0; i < this.numPrenotazioni; i++)
            if (this.prenotazioni[i].getTimeSlot().overlapsWith(ts))
                throw new IllegalArgumentException(
                        "Tentativo di aggiungere una prenotazione nell'aula in un timeslot che si sovrappone con un'altra prenotazione");
        // controllo se c'è spazio
        if (this.numPrenotazioni < this.prenotazioni.length) {
            // c'è spazio e la inserisco
            this.prenotazioni[this.numPrenotazioni] = new Prenotazione(this, ts,
                    docente, motivo);
            this.numPrenotazioni++;
        } else {
            // Il raddoppio copia i riferimenti, non gli oggetti.
            // non c'è spazio, raddoppio l'array
            this.prenotazioni = (Prenotazione[]) raddoppia(this.prenotazioni);
            // ora c'è spazio e inserisco
            this.prenotazioni[this.numPrenotazioni] = new Prenotazione(this, ts,
                    docente, motivo);
            this.numPrenotazioni++;
        }
    }

    /*
     * Metodo per raddoppiare gli array facilities e prenotazioni
     */
    private Object[] raddoppia(Object[] array) {
        if (array == null)
            throw new NullPointerException("Array da raddoppiare nullo");
        if (array instanceof Facility[]) {
            Facility[] array2 = new Facility[array.length * 2];
            for (int i = 0; i < array.length; i++) {
                array2[i] = (Facility) array[i];
            }
            return array2;
        }
        if (array instanceof Prenotazione[]) {
            Prenotazione[] array2 = new Prenotazione[array.length * 2];
            for (int i = 0; i < array.length; i++) {
                array2[i] = (Prenotazione) array[i];
            }
            return array2;
        }

        return null;
    }
}
