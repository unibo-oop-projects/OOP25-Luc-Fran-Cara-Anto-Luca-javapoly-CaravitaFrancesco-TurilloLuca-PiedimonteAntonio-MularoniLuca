# Modulo Player (Branch: LucaTurillo)

Questo modulo gestisce la logica dei giocatori, la gestione degli stati (libero, prigione, bancarotta) tramite il Pattern State e la creazione delle pedine tramite il Factory Method.

## Struttura del Package

Il codice è diviso in:

- **`it.unibo.javapoly.model.api`**: Contiene le interfacce pubbliche e l'enum TokenType. È l'unico pacchetto che gli altri moduli dovrebbero importare.

- **`it.unibo.javapoly.model.impl`**: Contiene le implementazioni concrete, i Singleton degli stati e la Factory.

## Come Integrare il Modulo

### 1. Creazione di un Giocatore

Per istanziare un giocatore, usa la classe PlayerImpl. È necessario specificare il nome, il bilancio iniziale e il tipo di pedina.

```java
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.TokenType;
import it.unibo.javapoly.model.impl.PlayerImpl;

Player p1 = new PlayerImpl("Luca", 1500, TokenType.CAR);
```

### 2. Gestione del Turno (Integrazione Controller)

**IMPORTANTE**: Non chiamare mai direttamente il metodo `move()`. Il movimento deve essere gestito dal metodo `playTurn()`. Questo perché il giocatore potrebbe essere in Prigione o in Bancarotta, e solo `playTurn` sa se il movimento è consentito o meno.

Esempio di integrazione nel MatchController:

```java
// Recupera i risultati dai dadi
int result = diceThrow.throwAll();
boolean isDouble = diceThrow.isDouble();

// Il metodo playTurn gestirà automaticamente:
// - Movimento (se Libero)
// - Tentativo di uscita (se in Prigione)
// - Nulla (se in Bancarotta)   
currentPlayer.playTurn(result, isDouble);
```

### 3. Pattern State

Il giocatore cambia stato automaticamente. Gli stati disponibili (accessibili tramite `player.getState()`) sono:

- **FreeState**: Movimento normale.

- **JailedState**: Movimento bloccato (esce solo con doppio o dopo 3 turni).

- **BankruptState**: Giocatore rimosso dai turni attivi.

### 4. Pattern Factory (Creazione Pedine)

Le pedine (Token) sono immutabili e create tramite una Factory per nascondere l'implementazione concreta.

Usare `TokenType` (Enum) per scegliere la grafica (es. `CAR`, `HAT`, `DOG`, ecc..).

### Riferimenti alle Proprietà (Owner)

Per evitare dipendenze circolari e problemi di serializzazione:

- Le Proprietà memorizzano l'owner tramite ID (String) (il nome del giocatore).

- Il Player NON mantiene una lista di oggetti Property. Se serve sapere cosa possiede un giocatore, filtrare le caselle del tabellone tramite il Controller.

### Factory delle Pedine

Le pedine (Token) sono gestite internamente. Se serve aggiungere nuove tipologie, modificare l'enum TokenType e la TokenFactory nel package `.impl`. Il costruttore di TokenImpl è protetto per impedire creazioni non autorizzate fuori dalla factory.

### 4. Accesso alla Posizione Attuale del Giocatore

Per ottenere la posizione attuale del giocatore sul tabellone, puoi utilizzare il metodo `getCurrentPosition()` della classe `PlayerImpl`. Questo metodo restituisce un intero che rappresenta l'indice della casella in cui si trova il giocatore.
Esempio di utilizzo:

```java
int posizioneAttuale = p1.getCurrentPosition();
```

## 5. Pattern Observer (Notifiche dei Cambiamenti)

Il modulo Player implementa il **Pattern Observer** per notificare altri componenti del sistema quando lo stato del giocatore cambia, senza creare accoppiamento stretto tra i moduli.

### Quando Vengono Inviate le Notifiche

Il `PlayerImpl` notifica automaticamente gli observer nei seguenti casi:

- **Movimento del giocatore**: Quando il giocatore si sposta da una posizione all'altra sul tabellone (metodo `onPlayerMoved`).
- **Cambio di bilancio**: Quando il giocatore riceve denaro (metodo `onBalanceChanged`).

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
