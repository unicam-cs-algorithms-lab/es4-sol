# Esercitazione 4 – Aule, prenotazioni e servizi

## Obiettivi didattici

Questa esercitazione consolida i concetti delle lezioni su classi, stato, incapsulamento, uguaglianza, ordinamento ed **ereditarietà**. Introduce inoltre la gestione esplicita di una collezione di riferimenti mediante **array**, prima di passare nella prossima lezione alle interfacce `List<E>` e all'implementazione `ArrayList<E>` del Java Collections Framework.

Gli obiettivi sono:

- distinguere **superclasse astratta** e **sottoclassi concrete**, usare `extends`, `super(...)`, overriding e dispatch dinamico;
- distinguere `Facility.equals` (identità per codice) da `Facility.satisfies` (compatibilità di una richiesta);
- riutilizzare i contratti di `equals`, `hashCode`, `Comparable` e `compareTo` di `TimeSlot` e `Prenotazione`;
- preservare l'immutabilità di `TimeSlot` con copie difensive di `GregorianCalendar`;
- mantenere negli array di `Aula` gli invarianti relativi a **capacità** (`array.length`) e **dimensione logica** (`numFacilities`, `numPrenotazioni`);
- implementare ricerca lineare, controllo dei duplicati, verifica dei vincoli e **raddoppio della capacità** quando necessario;
- usare i test JUnit 5 per verificare casi normali, casi limite, eccezioni e sequenze di operazioni.

## Modello e classi

- `Facility`: superclasse astratta; il codice identifica la facility. Il metodo astratto `satisfies(Facility)` è il punto di estensione polimorfico.
- `PresenceFacility`: soddisfa soltanto una richiesta di presenza con lo stesso codice.
- `QuantitativeFacility`: soddisfa una richiesta quantitativa con lo stesso codice se la quantità disponibile è almeno quella richiesta.
- `Aula`: identificata dal nome, contiene un array di facility e uno di prenotazioni, con contatori separati e capacità raddoppiata quando l'array è pieno.
- `Prenotazione`: identificata da aula e intervallo; docente e motivo non definiscono l'identità e sono modificabili.
- `TimeSlot`: intervallo temporale con sovrapposizione e tolleranza di 5 minuti interi, già affrontato nell'esercitazione 3.

### Array: capacità e dimensione logica

All'inizio `facilities.length == 5`, ma `numFacilities == 0`. Le posizioni effettivamente occupate sono soltanto quelle con indici da `0` incluso a `numFacilities` escluso. Lo stesso vale per le prenotazioni (capacità iniziale 100). Se un array è pieno, il codice alloca un array doppio, **copia i riferimenti** e sostituisce il riferimento al vecchio array. Non vengono copiati gli oggetti stessi.

Con gli array dobbiamo implementare manualmente controlli di capacità, copia e contatori. Nel passaggio futuro a `ArrayList<E>`, `add`, `size` e la gestione automatica della capacità semplificheranno questa logica. La ricerca dei duplicati e il controllo delle sovrapposizioni continueranno comunque a richiedere un ragionamento sul contratto e sulla complessità.

### Attenzione: getter che espongono gli array

Il contratto originario di `Aula.getFacilities()` e `Aula.getPrenotazioni()` restituisce **l'array interno**, comprese le posizioni non occupate. Questo permette di osservare la capacità, ma espone lo stato a modifiche esterne e costituisce una limitazione intenzionalmente evidenziata dell'API esistente. **Non modificare gli array restituiti**: il loro contenuto deve essere aggiornato tramite i metodi della classe. In una progettazione più robusta useremmo viste non modificabili o copie difensive, evitando di esporre l'array interno.

## Contratti da osservare

- `equals` e `hashCode` devono utilizzare gli stessi dati; `compareTo` deve essere coerente con l'identità logica.
- `Facility` confronta solo il codice, anche tra sottoclassi differenti; `satisfies` ha invece regole differenti per presenza e quantità.
- Una prenotazione che supera la tolleranza di sovrapposizione è rifiutata senza modificare il numero di prenotazioni.
- Per `TimeSlot`, due intervalli a contatto restituiscono `-1` minuti di sovrapposizione; una sovrapposizione positiva inferiore a un minuto restituisce `0`. `overlapsWith` è vero solo se i minuti interi superano strettamente 5.
- L'API originale non impone una quantità non negativa a `QuantitativeFacility`, né vieta `null` nei setter di `Prenotazione`: non introdurre restrizioni ulteriori senza modificare esplicitamente il contratto.

## Svolgimento

Leggere prima questa traccia e le Javadoc, poi implementare i metodi contrassegnati `TODO` **senza cambiare package, firme, classi o test**. Per `TimeSlot` e parte di `Prenotazione` si può riutilizzare e migliorare il codice dell'esercitazione 3. Procedere per piccoli passi e verificare frequentemente i test.

Il progetto richiede Java 8 e Maven. Dalla radice del progetto:

```bash
mvn test
```

I test automatici sono un supporto all'autovalutazione e non sostituiscono la lettura dei contratti delle API. L'eventuale presenza di test ulteriori non è esclusa.

## Consegna

La consegna avviene **esclusivamente nel compito corrispondente su Moodle**, entro la scadenza indicata. Caricare le **sei classi Java** richieste (`Aula.java`, `Facility.java`, `PresenceFacility.java`, `QuantitativeFacility.java`, `Prenotazione.java`, `TimeSlot.java`) con i TODO implementati. Non è necessario consegnare l'intero progetto Maven, i test, `pom.xml` o il README. Commit e push in un repository personale sono facoltativi e **non costituiscono consegna**.

## Documentazione API

Per generare la Javadoc dalla radice del progetto:

```bash
javadoc -encoding UTF-8 -charset UTF-8 -docencoding UTF-8 -d docs \
  -sourcepath src/main/java -subpackages it.unicam.cs.asdl.es4
```

Le Javadoc descrivono il contratto pubblico; i commenti ordinari spiegano scelte algoritmiche e invarianti.
