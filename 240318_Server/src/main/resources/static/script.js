const produktForm = document.querySelector('form');
const produktInput = document.getElementById('produkt-input');
const produktListUL = document.getElementById('produkt-list');

//let allProdukte = getProdukte();
let allProdukte = [];

// Beim Laden der Seite Produkte aus der Datenbank laden
update();
/*(async () => {
    try {
        await getDatenFromMongoDB();    // Daten aus MongoDB abrufen
        updateProduktList();            //Oberfläche aktualisieren
    } catch (error) {
        console.error('Fehler beim Abrufen der Daten:', error);
    }
})();*/

produktForm.addEventListener('submit', function (e){
    e.preventDefault();
    addProdukt();
})

function addProdukt(){
    const produktText = produktInput.value.trim();

    //nur wenn der Benutzer etwas eingegeben hat
    if(produktText.length > 0){
        let produktObject = {
            produkt: produktText
        };

        //auf Endpoint für POST zugreifen
        saveProdukte(produktObject);
        /*let xhr = new XMLHttpRequest();
        xhr.open('POST', '/api/produkt', true);
        xhr.setRequestHeader('Content-Type', 'application/json');

        xhr.send(JSON.stringify(produktObject));*/

        /*produktObject = {
            id: "",
            text: produktText,
            completed: false
        }*/

        //allProdukte.push(produktObject);
        //updateProduktList();
        update();
        //saveProdukte();
        produktInput.value = "";
    }
}

function updateProduktList(){
    produktListUL.innerHTML = "";
    allProdukte.forEach(function(p) {
        produktItem = createProduktItem(p.produkt, p.id);
        produktListUL.append(produktItem);
    })
}

function createProduktItem(produkt, id){
    const produktId = "produkt-"+id;
    const produktLI = document.createElement("li");
    const produktText = produkt;

    //className auf "produkt" damit die CSS-Styles wirken
    produktLI.className = "produkt";
    produktLI.innerHTML = ` 
                <input type="checkbox" id="${produktId}">
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
    const deleteButton = produktLI.querySelector(".delete-button");
    deleteButton.addEventListener("click", ()=>{
        deleteProduktItem(produktIndex);
    })
    const editButton = produktLI.querySelector(".edit-button");
    editButton.addEventListener("click", ()=>{
        editProduktItem(produktIndex);
    })
    const checkbox = produktLI.querySelector("input");
    checkbox.addEventListener("change", ()=>{
        allProdukte[produktIndex].completed = checkbox.checked;
        saveProdukte();
    })
    checkbox.checked = produkt.completed;

    return produktLI;
}

/*function deleteProduktItem(produktIndex){
    allProdukte = allProdukte.filter((_, i)=> i !== produktIndex);
    saveProdukte();
    updateProduktList();
}
function editProduktItem(produktIndex){
    allProdukte = allProdukte.filter((_, i)=> i !== produktIndex);
    saveProdukte();
    updateProduktList();
}*/
function saveProdukte(produktObject){
    /*const produkteJson = JSON.stringify(allProdukte);
    localStorage.setItem("produkte", produkteJson);*/
    //auf Endpoint für POST zugreifen
    let xhr = new XMLHttpRequest();
    xhr.open('POST', '/api/produkt', true);
    xhr.setRequestHeader('Content-Type', 'application/json');

    xhr.send(JSON.stringify(produktObject));
}

function getProdukte(){
    /*const produkte = localStorage.getItem("produkte") || "[]";
    return JSON.parse(produkte);*/

    let produkte = [];

    let xhr = new XMLHttpRequest();
    xhr.open('GET', 'api/produkte', true);
    xhr.setRequestHeader('Content-Type', 'application/json');

    xhr.onload = function() {
        if (xhr.status >= 200 && xhr.status < 300) {
            produkte = JSON.parse(xhr.responseText);      //alle Produkte aus er Datenbank zurückgeben
        } else {
            console.error('Fehler beim Abrufen der Produkte:', xhr.statusText);
        }
    };

    xhr.onerror = function() {
        console.error('Netzwerkfehler beim Abrufen der Produkte.');
    };

    xhr.send();
    return produkte;

}

async function getDatenFromMongoDB() {
    try {
        const response = await fetch('/api/produkte');
        if (!response.ok) {
            throw new Error('Fehler beim Abrufen der Daten');
        }
        //const daten = await response.json();
        //return daten;
        allProdukte = await response.json();
    } catch (error) {
        console.error('Fehler beim Abrufen der Daten:', error);
        throw error;
    }
}

async function update() {
    try {
        await getDatenFromMongoDB().then(r => updateProduktList()); // Daten aus MongoDB abrufen & Oberfläche aktualisieren
        //console.log('Daten aus der MongoDB:', allProdukte); // auf das Ergebnis warten und dann weitermachen
    } catch (error) {
        console.error('Fehler beim Abrufen der Daten:', error);
        // Fehlerbehandlung
    }
}
