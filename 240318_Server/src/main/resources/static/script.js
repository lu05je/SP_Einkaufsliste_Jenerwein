const produktForm = document.querySelector('form');
const produktInput = document.getElementById('produkt-input');
const produktListUL = document.getElementById('produkt-list');

const editDialog = document.getElementById('editDialog');
const dialogText = document.getElementById('dialogText');
const cancelBtn = document.getElementById('cancelBtn');
const confirmBtn = document.getElementById('confirmBtn');

//Liste aller Produkte
let allProdukte = [];

// Beim Laden der Seite Produkte aus der Datenbank laden
update();

produktForm.addEventListener('submit', function (e){
    e.preventDefault();     //Standartverhalten verhindern
    addProdukt();
})

function addProdukt() {
    const produktText = produktInput.value.trim();

    // Nur wenn der Benutzer etwas eingegeben hat
    if (produktText.length > 0) {
        let produktObject = `{
                "produkt": "${produktText}",
                "status": false
            }`;

        // Auf Endpoint für POST zugreifen und Callback verwenden
        saveProdukte(produktObject, function(error) {
            if (error) {
                console.error('Fehler beim Speichern des Produkts:', error);
                // Fehlerbehandlung
            } else {
                // Nachdem das Produkt gespeichert wurde, die Oberfläche aktualisieren
                update();
                produktInput.value = "";
            }
        });
    }
}

function updateProduktList(){
    produktListUL.innerHTML = "";           //Produktliste leeren

    //Listenelemente neu erstellen
    allProdukte.forEach(function(p) {
        produktItem = createProduktItem(p.produkt, p.id, p.status);
        produktListUL.append(produktItem);
    })
}

function createProduktItem(produkt, id, isChecked){
    const produktId = "produkt-"+id;
    const produktLI = document.createElement("li");
    const produktText = produkt;


    //className auf "produkt" damit die CSS-Styles wirken
    produktLI.className = "produkt";

    //Listenelement erstellen
    produktLI.innerHTML = ` 
                <input type="checkbox" id="${produktId}" ${isChecked ? "checked" : ""}>
                <label class="custom-checkbox" for="${produktId}">
                    <svg fill="transparent" xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 -960 960 960" width="24">
                        <path d="M382-240 154-468l57-57 171 171 367-367 57 57-424 424Z"/>
                    </svg>
                </label>
                <label for="${produktId}" class="produkt-text">
                    ${produktText}
                </label>
                <button class="edit-button">
                    <svg fill="var(--secondary-color)" xmlns="http://www.w3.org/2000/svg" height="48" viewBox="0 -960 960 960" width="48">
                        <path d="M180-180h44l472-471-44-44-472 471v44Zm-60 60v-128l575-574q8-8 19-12.5t23-4.5q11 0 22 4.5t20 12.5l44 44q9 9 13 20t4 22q0 11-4.5 22.5T823-694L248-120H120Zm659-617-41-41 41 41Zm-105 64-22-22 44 44-22-22Z"/>
                    </svg>
                </button>
                <button class="delete-button">
                    <svg fill="var(--secondary-color)" xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 -960 960 960" width="24">
                        <path d="M280-120q-33 0-56.5-23.5T200-200v-520h-40v-80h200v-40h240v40h200v80h-40v520q0 33-23.5 56.5T680-120H280Zm400-600H280v520h400v-520ZM360-280h80v-360h-80v360Zm160 0h80v-360h-80v360ZM280-720v520-520Z"/>
                    </svg>
                </button>
    `

    //Event-Listener für den Delete-Button
    const deleteButton = produktLI.querySelector(".delete-button");
    deleteButton.addEventListener("click", ()=>{
        deleteProduktItem(id)
            .then(() => {
                console.log('Produkt wurde erfolgreich gelöscht');
                update();
            })
            .catch(error => {
                console.error('Es gab einen Fehler beim Löschen des Produkts:', error);
            });
    })

    //Event-Listener für den Edit-Button
    const editButton = produktLI.querySelector(".edit-button");
    editButton.addEventListener("click", ()=>{

        // Den Dialog öffnen und den aktuellen Text des Produkts im Eingabefeld anzeigen
        dialogText.value = produktText;
        editDialog.showModal();

        //Bei Abbrechen -> Dialog schließen
        cancelBtn.addEventListener('click', () => {
            editDialog.close();
        })

        //Event-Listener für den Bestätigungsbutton im Dialog
        confirmBtn.addEventListener('click', () => {
            // Den neuen Text aus dem Dialogfeld abrufen
            const newProduktText = dialogText.value.trim();

            // Überprüfen, ob der neue Text nicht leer ist
            if (newProduktText !== '') {
                // JSON mit den aktualisierten Produktinformationen erstellen
                const updatedJson = `{
                    "id": "${id}",
                    "produkt": "${newProduktText}",
                    "status": "${isChecked}"
                }`;

                editProduktItem(updatedJson);
            }
            else {
                // Wenn der neue Text leer ist, dem Benutzer eine Fehlermeldung anzeigen
                console.error('Der neue Produkttext darf nicht leer sein.');
            }
        });
    })

    //Event-Listener für die Checkbox
    const checkbox = produktLI.querySelector("input[type='checkbox']");
    checkbox.addEventListener("change", () => {
        const updatedJson = `{
            "id": "${id}",
            "produkt": "${produkt}",
            "status": "${checkbox.checked}"
        }`;
        editProduktItem(updatedJson);
    });

    return produktLI;
}

async function deleteProduktItem(id) {
    try {
        const response = await fetch(`/api/produkt/${id}`, {
            method: 'DELETE'
        });
        if (!response.ok) {
            throw new Error('Fehler beim Löschen des Produkts');
        }
        console.log('Produkt erfolgreich gelöscht');
    }
    catch (error) {
        console.error('Fehler beim Löschen des Produkts:', error);
        throw error;
    }
}

async function editProduktItem(produkt){
    try {
        const response = await fetch('/api/produkt', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: produkt
        });
        if (!response.ok) {
            throw new Error('Fehler beim Aktualisieren des Produkts');
        }
        else {
            update();       //Bei erfolgreicher Änderung -> Oberfläche aktualisieren
        }
    }
    catch (error) {
        console.error('Fehler beim Aktualisieren des Produkts:', error);
        throw error;
    }
}

function saveProdukte(produktObject, callback) {
    let xhr = new XMLHttpRequest();
    xhr.open('POST', '/api/produkt', true);     //true -> asynchron
    xhr.setRequestHeader('Content-Type', 'application/json');

    xhr.onload = function () {
        if (xhr.status >= 200 && xhr.status < 300) {
            // Erfolgreiche Anfrage
            callback(null); // null -> kein Fehler aufgetreten
        }
        else {
            // Anfrage fehlgeschlagen
            callback(xhr.statusText);
        }
    };
    xhr.onerror = function () {
        // Fehler bei der Anfrage
        callback(xhr.statusText);
    };

    xhr.send(produktObject);    //Anfrage an Server senden
}

async function getDatenFromMongoDB() {
    try {
        //alle Produkte aus der Datenbank lesen und in allProdukte speichern
        const response = await fetch('/api/produkte');
        if (!response.ok) {
            throw new Error('Fehler beim Abrufen der Daten');
        }
        allProdukte = await response.json();
    }
    catch (error) {
        console.error('Fehler beim Abrufen der Daten:', error);
        throw error;
    }
}

async function update() {
    try {
        await getDatenFromMongoDB().then(r => updateProduktList()); // Daten aus MongoDB abrufen & Oberfläche aktualisieren
    }
    catch (error) {
        console.error('Fehler beim Abrufen der Daten:', error);
    }
}
