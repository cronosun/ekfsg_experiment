# eKFSG Experiment

Video von der laufenden App:
https://youtu.be/6t7FUX9rqWE

## Was ist diese App?

Kleines Experiment mit einigen alternativen Code-Design-Ideen für eine eKFSG-App.

### Was ist es nicht
 
 * Definitiv kein fertiges App-Code-Design - es sind lediglich einige Ideen eingeflossen / als Experiment.
 * Der gezeigte Use-Case mit "Rechnung Einreichen" und "Genehmigen / Ablehnen" und dem Mailversand entspricht auch nicht einem richtigen eKFSG-Use-Case (lediglich ein wenig von eKFSG inspiriert).
 * Dinge - welche aus meiner Sicht - nicht interessant sind / waren hab ich nur kurz "hingehackt": Wie das User-Interface (da müsste ich mich genauer mit Vaadin befassen).

### Was ist es

 * Es ist eine lauffähige App mit einer Datenbankanbindung (Flyway ist in Verwendung).
 * Es ist eine testbare Anwendung.

### Was wird gezeigt / was sind die interessanten Punkte

Es handelt sich um EINE Anwendung (Externer-Zugang und Interner-Zugang) kombiniert (z.b. "Rechnung Einreichen" ist eine Funktion des Externen-Zugangs, "Rechnung Genehmigen" ist eine Funktion des Internen-Zugangs - Dabbawala). Was in diesem kleinen Experiment nicht gemacht wurde: Die Möglichkeit die Teile zu aktivieren / deaktivieren und die Datensynchronisation zwischen den beiden Teilen ... also eigentlich das was ich im "Verbesserungsvorschläge"-Dokument beschrieben habe, ist hier nicht umgesetzt (aber das wäre nicht komplex das auch umzusetzen). Müsste man haben, damit eine Version für das Internet ("Externer Zugang") und eines im internen Netz deployed werden kann ("Interner-Zugang").

**Testbarkeit**

Wohl am interssantesten sind die Tests, siehe:

 * `UseCaseUC478Test`
 * `UseCaseUC705Test`

...durch den einfachen Aufbau der App sind nun komplette Prozesse Testbar: Von der Eingabe der Rechnung ("Externer Zugang") über die Rechnungsfreigabe ("Genehmigung") im Internen-Zugang ist alles testbar. _Unmanaged Dependencies_ (wie Mail-Server) sind durch Simulatoren ersetzt worden. Ich hab den Mail-Sender mal als einfaches Beispiel genommen - eigentlich könnte man ziemlich dasselbe für _CMI/Axioma_ machen, um in Tests zu schauen, ob und welches Dossier auf _CMI/Axioma_ angekommen ist (ähnlich wie in den Tests geschaut wird, ob eine Mail gesendet wurde). Es sind zwar keine UI-Tests vorhanden, aber durch dieses Design ist es möglich sehr nahe am UI zu testen (und trozdem sind die Tests einfach zu erstellen und werden relativ schnell ausgeführt):

 * Die Views sind "dumm" und schlank. Sie beinhalten üblicherweise lediglich das Layout und das Binding an den Controller. Das wird unter anderem durch die Verwendung von reactive Streams erreicht (`RxJava` / `RxKotlin`). Dadurch ist es (einfach) möglich, dass lediglich die View Abhängigkeiten auf den Controller hat, der Controller nicht jedoch auf die View (der Controller kann somit in Tests unabhängig von der View getestet werden).
 * Die Controller sind in den Tests testbar (die Controller sind nahe an der View).
 * Das einzige, was nicht direkt getestet wird, sind die (schlanken & dummen) Views. Alles andere kann durch Tests abgedeckt werden.

Siehe als Beispiel:

 * `ExternalInvoiceController`: Controller für die Eingabe einer Rechnung im Externen-Zugang (das wird getestet - und natürlich auch alle Services, die dahinter verwendet werden).
 * `ExternalInvoiceView`: Dumme & schlanke View für die Eingabe einer Rechnung (das ist das Einzige, was in diesem Bereich nicht getestet wird).

Was an den beiden Test-Klassen (bitte anschauen!) `UseCaseUC478Test` & `UseCaseUC705Test` ebenfalls zu sehen ist:

 * Wir testen keine (oder wenige) technischen Dinge.
 * Die Tests bilden mehr oder weniger 1:1 die Spezifikation ab. Die einzelnen Testschritte entsprechen ziemlich genau den in der Spez beschriebenen Schritten / Schritte welche ein Benutzer durchführen würde.

Artikel zu diesem Thema (Testbare, Reactive Apps, z.b. mit `RxJava`, `RxKotlin`): https://medium.com/@ragunathjawahar/mvi-series-a-pragmatic-reactive-architecture-for-android-87ca176387d1

> In MVI, the data always flows from one part of the program to another in a single direction. This makes testing and debugging the app easy because the inputs and outputs of a given component or function are always well-defined and predictable.

> Simple — The beauty of the architecture is in its simplicity. It should be fairly easy for developers already using RxJava to understand and implement it. I also felt that, developers can but do not have to rely on a library to implement the pattern.

> Testable: Anything hard to test is not worth building. Reactive streams are very easy to test and a reactive architecture should exhibit the same attribute. I have also tried to make the test pyramid bottom-heavy so that most of the application can be unit tested. UI tests are necessary, but I wanted to keep them at a minimum.

Hier ein Beispiel-Test-Case (ja, dieser Test ist real und läuft durch):

```kotlin
    /**
     * Auszug aus der Spezifikation (oder dem Issue): "Der Benutzer A reicht über den externen Zugang eine Rechnung ein.
     * Der Mitarbeiter B (Kanton) sieht danach diese Rechnung in der Ansicht XY. Der Mitarbeiter B hat die Möglichkeit diese
     * Rechnung abzulehnen. Der Benutzer A wird per Mail informiert, dass die Rechnnung abgelehnt wurde."
     */
    @Test
    fun test_uc705_fua47() {
        navigator.clear()
        senderBackend.clearAll()

        // ---------------- Benutzer A ("externer Zugang")

        // navigieren auf die Rechnungseingabe vom "Externen Zugang"
        val externalInvoice = controllers.get.externalInvoice
        // Eingabe der Sozialversicherngsnummer
        externalInvoice.ssn.onNext("756.9217.0769.85")
        // Hochladen des Rechnungsdokuments
        TestFiles.simpleTextDocument.uploadToController(externalInvoice.invoiceDocumentController)
        // Einreichen der Rechnung
        externalInvoice.submit()

        // Nun sollte das System auf die Bestätigunsseite navigiert haben
        assertEquals(1, navigator.count(InvoiceAddedView.NAVIGATION))
        // ... und der Benutzer A hat auch eine Mail erhalten
        assertEquals(1, senderBackend.numberOfMatches(MailExample(subject = "Rechnung erfasst")))

        // ---------------- Mitarbeiter B ("interner Zugang")

        navigator.clear()
        senderBackend.clearAll()
        // navigieren auf die Seite "Rechnung genehmigen oder ablehnen."
        val approveReject = controllers.get.approveRejectInvoicePageController
        approveReject.onAttach()
        // sieht der Mitarbeiter die eingereichte Rechnung? (die mit der SSN "756.9217.0769.85")
        val entriesInTable = approveReject.invoiceListController.invoicesInTable.blockingFirst()
        val maybeInvoice = entriesInTable.find { it.ssn == "756.9217.0769.85" }
        assertNotNull(maybeInvoice)
        // Auswählen dieser Rechnung aus der Tabelle (selektieren)
        approveReject.approveRejectInvoiceController.approveOrRejectInvoice(InvoiceToApproveOrReject.Some(maybeInvoice!!.source))
        // nun die Rechnung ablehnen
        approveReject.approveRejectInvoiceController.reject()

        // ---------------- Benutzer A

        // der Benutzer A sollte nun ne Mail erhalten haben, dass seine Rechnung abgelehnt wurde
        assertEquals(1, senderBackend.numberOfMatches(MailExample(subject = "Rechung Abgelehnt")))
    }
```

**Typisierung**

Stakre Typisierung, kaum plain `String`, `UUID` und dergleichen in Verwendung. Stattdessen, siehe folgende Klassen:

 * `Ssn`
 * `FileContentId`
 * `Filename`
 * `MimeType`
 * `Iban`
 * `MaybeIban`
 * `CurrencyAmount`
 * `MaybeCurrencyAmount`

**Entwickler-Experience**

Kein komplexes Setup notwendig (in der IDE auf "Run" klicken - die App läuft - oder sollte es zumindest). Hot-Reloading (Server & Client) sollte möglich sein (habs nicht ausprobiert, aber sollte laut Doku gehen). Startzeit ist realtiv vernünftig. Ja, die Zeitangabe ist von einem M1-Mac, aber ich denke mit etwas Optimierung wird man das auch auf einem Nicht-Mac auf unter 10 Sekunden bringen:

```
EkfsgApplicationKt in 3.459 seconds (JVM running for 3.707)
```

**Strukturierung Packages**

Strukturiert (Packages) ist das Zeugs nach Fachlichkeit:
 
 * `/mail` (Mail Versenden & Anzeige Postausgang)
 * `/invoice` (Rechnungseingabe / Freigabe / Ablehnung)
 * `/file` (Dokumentverwaltung)

... sonst aber nicht zu viel Gewicht auf Strukturierung gelegt. Das müsste sicherlich verbessert werden (z.b. die Sub-Packages `ui` ... mit den Views und den Controllern ... vielleicht in ein einges Maven-Modul? ... und das `shared`-Package, sinnvoll?).

**Strukturierung**

Was auch ziemlich anders zu der echten eKFSG-App ist, sind die folgenden Punkte:

Die Service-Methoden entsprechen mehr den Use-Cases. Beispiel: Im `InvoiceService` gibts eine Methode `addInvoice`, die ist dazu da, eine neue Rechnung im Externen Zugang einzureichen. Als Eingabe nimmt diese Methode auch genau ein - für diesen Case - exakt passendes Pojo (oder Poko) entgegen:

```kotlin
data class NewExternalInvoice(
    override val amount: MaybeCurrencyAmount,
    override val comment: String,
    override val document: FileId,
    override val ssn: Ssn,
    override val iban: MaybeIban
) : Invoice
```

...anstatt wie im echten eKFSG (oft) gemacht eine `save`-Methode welche ein Entity entgegennimmt (wo dann unzählige Müll-Felder vorhanden sind, welche für diesen Use-Case belanglos sind). Wieso ist es exakt auf diesen Use-Case zugeschitten?

 * Keine `ID` ist vorhanden im `NewExternalInvoice`: Die braucht es auch nicht (denn es handelt sich ja immer um eine NEUE Rechnung wenns über den Externen-Zugang reinkommt).
 * Kein `status` ist vorhanden (obwohl es den Status am Entity gibt). Aber: Wird dieser Use-Case durchgeführt, so ist der Status fix (ist immer NEW) - also ist er auch am Pojo nicht vorhanden.
 * Es ist auch direkt erkennbar, welche Dinge erforderlich sind und welche nicht:
   * `amount: MaybeCurrencyAmount`: **Maybe**: Betrag ist optional, im Externen Zugang muss nicht zwingend ein Betrag eingegeben werden.
   * `ssn: Ssn`: Die Sozialversicherungsnummer ist erforderlich.
   * `iban: MaybeIban`: **Maybe**: Die IBAN ist optional im Externen-Zugang.

... führen wir einen anderen Use-Case durch - wie das Genehmigen (oder Freigeben einer Rechnung) im Internen-Zugang, wird ein anderes Pojo verwendet (und auch eine andere Service-Methode, `approveInvoice`):

```kotlin
data class InvoiceApproval(
    val id: InvoiceId,
    val iban: Iban,
    val amount: CurrencyAmount
)
```

... zu sehen ist nun, dass `iban` und `amount` nicht mehr **Maybe** sind: Wird die Rechnung genehmigt, so müssen diese Werte vorhanden sein (damit FIS weiss wohin das Geld muss und wieviel).

... die Entities (interne Datenbank-Details) werden auch nicht aus dem Service geleakt (die sind private innerhalb eines Services).

Zu diesem Thema (mindestens verwandt) ist dieser Artikel lesenswert (hat auch ein Video dazu): https://codeopinion.com/avoid-entity-services-by-focusing-on-capabilities/

> If you’re looking at an API, it would contain just CRUD type methods <..> The alternative is to focus on the capabilities of your service. <..> A service is the authority of a set of business capabilities. <..> If you decompose the capabilities that you need to expose to users, they are driven by commands/actions/tasks. What are the things users are trying to do?

... DTOs und Mapper hingegen brauchts bei diesem App-Design nicht (Frontend und Backend in einer App). Ich würde auch so weit gehen, dass auch für eine Spring-Backend-Angular-Frontent-App keine DTOs notwending sind bei diesem Design. Wieso? DTOs habe sicherlich ihre Berechtigung, falls damit sichergestellt werden soll, dass wenig interne App-Details in ein Public-API leaken und das Public-API stabil gehalten werden muss. Das haben wir jedoch auch bei der richtigen eKFSG-App nicht: Wir haben kein Public-API (werder zwichen Backend-Frontend, noch für die Datensynchronisierung): Wir deployen sowieso immer synchron, d.h. das API muss nich stabil gehalten werden (kein öffentlicher Contract). D.h. wenn auch in der richtigen eKFSG-App Value-Objects für die Service-Calls verwendet werden (und nicht Entities nach Aussen leaken), so brauchts zusätzlich zu den Value-Objekten nicht noch mal zusätzlich DTOs (die Value-Objekte können sorglos auch als DTO verwendet werden).

**Kotlin**

Kotlin wird in dieser App verwendet. Muss aber nicht zwingen sein - das lässt sich auch mit Java problemlos so umsetzen (gibt einfach ein wenig mehr Tipparbeit). Schön sind die typsicheren ADTs, siehe dazu:

 * Z.b. `MaybeIban`
 * Die `when`-Expression von Kotlin (stellt automatisch sicher, dass alle Fälle behandelt werden).

## Alternativen

Es müssen nicht alle diese Punkte umgesetzt werden, auch das ist denkbar:

 * Angular-Frontend belassen (macht halt das Testen schwieriger)
 * Kein Kotlin
 * Blazor Server