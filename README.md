# 👤 Modulo Player (Branch: LucaTurillo)

Questo modulo gestisce la logica dei giocatori, la gestione degli stati (libero, prigione, bancarotta) tramite il Pattern State e la creazione delle pedine tramite il Factory Method.

## 📂 Struttura del Package

Il codice è diviso in:

- **`it.unibo.javapoly.model.api`**: Contiene le interfacce pubbliche e l'enum TokenType. È l'unico pacchetto che gli altri moduli dovrebbero importare.

- **`it.unibo.javapoly.model.impl`**: Contiene le implementazioni concrete, i Singleton degli stati e la Factory.

## 🛠 Come Integrare il Modulo

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

### 🔑 Riferimenti alle Proprietà (Owner)

Per evitare dipendenze circolari e problemi di serializzazione:

- Le Proprietà memorizzano l'owner tramite ID (String) (il nome del giocatore).

- Il Player NON mantiene una lista di oggetti Property. Se serve sapere cosa possiede un giocatore, filtrare le caselle del tabellone tramite il Controller.

### 📦 Factory delle Pedine

Le pedine (Token) sono gestite internamente. Se serve aggiungere nuove tipologie, modificare l'enum TokenType e la TokenFactory nel package `.impl`. Il costruttore di TokenImpl è protetto per impedire creazioni non autorizzate fuori dalla factory.

### 4. Accesso alla Posizione Attuale del Giocatore

Per ottenere la posizione attuale del giocatore sul tabellone, puoi utilizzare il metodo `getCurrentPosition()` della classe `PlayerImpl`. Questo metodo restituisce un intero che rappresenta l'indice della casella in cui si trova il giocatore.
Esempio di utilizzo:

```java
int posizioneAttuale = p1.getCurrentPosition();
```
