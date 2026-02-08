# Modulo Player (Branch: LucaTurillo)

Questo modulo gestisce la logica dei giocatori, la gestione degli stati (libero, prigione, bancarotta) tramite il Pattern State e la creazione delle pedine tramite il Factory Method.

## Struttura del Package

Il codice è diviso in:

- **`it.unibo.javapoly.model.api`**: Contiene le interfacce pubbliche e l'enum TokenType. È l'unico pacchetto che gli altri moduli dovrebbero importare.

- **`it.unibo.javapoly.model.impl`**: Contiene le implementazioni concrete, i Singleton degli stati e la Factory.

## Come Integrare il Modulo

### 1. Creazione di un Giocatore

La classe `PlayerImpl` offre due costruttori per coprire diverse esigenze di utilizzo:

#### 1. Costruttore Standard (Default)

Da utilizzare per l'avvio di una **nuova partita standard**. Inizializza automaticamente il giocatore con il bilancio iniziale previsto dalle regole (es. 1500$), nascondendo questo dettaglio implementativo al chiamante (Controller o View).

```java
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.TokenType;
import it.unibo.javapoly.model.impl.PlayerImpl;

// Crea un giocatore con nome, pedina e bilancio standard
Player p1 = new PlayerImpl("Luca", TokenType.CAR);
```

#### 2. Costruttore Avanzato (Custom Balance)

Permette di specificare manualmente il bilancio iniziale. Basandosi sulla Javadoc, questo costruttore è progettato per:

- **Unit Testing**: Per creare scenari di test con condizioni di bilancio specifiche.
- **Persistenza/Serializzazione**: Per ricaricare lo stato di un giocatore da un salvataggio (es. JSON), preservando i soldi che aveva.
- **Varianti del Gioco**: Per supportare regole della casa con soldi iniziali diversi.

```java
// Crea un giocatore con un bilancio specifico (es. 500$)
Player p2 = new PlayerImpl("Mario", 500, TokenType.HAT);
```

### 2. Gestione del Turno (Integrazione Controller)

**IMPORTANTE**: Non chiamare mai direttamente il metodo `move()`. Il movimento deve essere gestito dal metodo `playTurn()`. Questo perché il giocatore potrebbe essere in Prigione o in Bancarotta, e solo `playTurn` sa se il movimento è consentito o meno.

**AGGIORNAMENTO**: I metodi `playTurn()` e `move()` ora accettano solo la **posizione di destinazione** come parametro, non più il risultato del lancio del dado. La logica di controllo su quale azione può essere eseguita è delegata interamente allo stato corrente del giocatore tramite il Pattern State.

Esempio di integrazione nel MatchController:

```java
// Calcola la posizione di destinazione in base al lancio dei dadi
int result = diceThrow.throwAll();
boolean isDouble = diceThrow.isDouble();
int destination = (currentPlayer.getCurrentPosition() + result) % boardSize;

// Il metodo playTurn gestirà automaticamente:
// - Movimento (se Libero)
// - Tentativo di uscita (se in Prigione)
// - Nulla (se in Bancarotta)
// Lo stato del giocatore decide se e come eseguire l'azione
currentPlayer.playTurn(destination, isDouble);
```

### 3. Pattern State

Il giocatore cambia stato automaticamente. Gli stati disponibili (accessibili tramite `player.getState()`) sono:

- **FreeState**: Movimento normale.

- **JailedState**: Movimento bloccato (esce solo con doppio o dopo 3 turni).

- **BankruptState**: Giocatore rimosso dai turni attivi.

Ogni stato implementa la propria logica per `playTurn()`, verificando internamente se l'azione richiesta può essere eseguita.

### 4. Pattern Factory (Creazione Pedine)

Le pedine (Token) sono immutabili e create tramite una Factory per nascondere l'implementazione concreta.

Usare `TokenType` (Enum) per scegliere la grafica (es. `CAR`, `HAT`, `DOG`, ecc..).

### Riferimenti alle Proprietà (Owner)

Per evitare dipendenze circolari e problemi di serializzazione:

- Le Proprietà memorizzano l'owner tramite ID (String) (il nome del giocatore).

- Il Player NON mantiene una lista di oggetti Property. Se serve sapere cosa possiede un giocatore, filtrare le caselle del tabellone tramite il Controller.

### Factory delle Pedine

Le pedine (Token) sono gestite internamente. Se serve aggiungere nuove tipologie, modificare l'enum TokenType e la TokenFactory nel package `.impl`. Il costruttore di TokenImpl è protetto per impedire creazioni non autorizzate fuori dalla factory.

### 5. Accesso alla Posizione Attuale del Giocatore

Per ottenere la posizione attuale del giocatore sul tabellone, puoi utilizzare il metodo `getCurrentPosition()` della classe `PlayerImpl`. Questo metodo restituisce un intero che rappresenta l'indice della casella in cui si trova il giocatore.
Esempio di utilizzo:

```java
int posizioneAttuale = p1.getCurrentPosition();
```

## 6. Pattern Observer (Notifiche dei Cambiamenti)

Il modulo Player implementa il **Pattern Observer** per notificare altri componenti del sistema quando lo stato del giocatore cambia, senza creare accoppiamento stretto tra i moduli.

### Quando Vengono Inviate le Notifiche

Il `PlayerImpl` notifica automaticamente gli observer nei seguenti casi:

- **Movimento del giocatore**: Quando il giocatore si sposta da una posizione all'altra sul tabellone (metodo `onPlayerMoved`).
- **Cambio di bilancio**: Quando il giocatore riceve denaro (metodo `onBalanceChanged`).
- **Cambio di stato**: Quando il giocatore passa da uno stato all'altro (FreeState, JailedState, BankruptState) tramite il metodo `onStateChanged`.

### Come Implementare un Observer

Per ricevere notifiche sui cambiamenti del giocatore, crea una classe che implementi l'interfaccia `PlayerObserver`:

```java
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.PlayerObserver;

public class MyPlayerObserver implements PlayerObserver {
    
    @Override
    public void onPlayerMoved(Player player, int oldPosition, int newPosition) {
        // Gestisci il movimento del giocatore
        System.out.println(player.getName() + " si è mosso da " + 
                          oldPosition + " a " + newPosition);
        // Ad esempio: aggiorna la GUI, registra su log, ecc.
    }
    
    @Override
    public void onBalanceChanged(Player player, int newBalance) {
        // Gestisci il cambio di bilancio
        System.out.println(player.getName() + " ora ha " + newBalance + "$");
        // Ad esempio: aggiorna l'interfaccia utente
    }

    @Override
    public void onStateChanged(Player player, String newState) {
        // Gestisci il cambio di stato
        System.out.println(player.getName() + " è ora in stato: " + newState);
        // Ad esempio: mostra un messaggio, aggiorna la GUI, ecc.
    }
}
```

### Come Registrare un Observer

Una volta implementato l'observer, registralo sul giocatore di interesse:

```java
Player p1 = new PlayerImpl("Luca", 1500, TokenType.CAR);
MyPlayerObserver observer = new MyPlayerObserver();

// Registra l'observer
p1.addObserver(observer);

// Da questo momento in poi, l'observer riceverà tutte le notifiche
p1.move(5); // Trigger: onPlayerMoved
p1.receiveMoney(200); // Trigger: onBalanceChanged
```

### Come Rimuovere un Observer

Se non hai più bisogno di ricevere notifiche, rimuovi l'observer:

```java
p1.removeObserver(observer);
```

### Casi d'Uso Tipici

- **Controller/View**: Per aggiornare l'interfaccia grafica quando un giocatore si muove o cambia bilancio.
- **Sistema di Logging**: Per registrare tutte le azioni dei giocatori durante la partita.
- **Sistema di Statistiche**: Per calcolare e aggiornare statistiche in tempo reale.
- **Salvataggio Automatico**: Per salvare lo stato di gioco dopo ogni cambio significativo.

**Nota**: Un singolo giocatore può avere più observer registrati contemporaneamente. Tutti riceveranno le notifiche nello stesso ordine di registrazione.
 Modulo Player (Branch: LucaTurillo)
