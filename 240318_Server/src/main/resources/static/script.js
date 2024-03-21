let shoppingItems = getShoppingItemsFromLocalStorage();

updateList();

function getShoppingItemsFromLocalStorage() {
    let items = localStorage.getItem('shoppingItems');

    if (items == null || items == '') {
        items = [];
    } else {
        items = items.split(',');
    }

    return items;
}

function addNewItem() {
    let item = prompt('Was möchtest du hinzufügen?');

    if (item != null) {
        shoppingItems.push(item);
        localStorage.setItem('shoppingItems', shoppingItems);
    }
}

function removeItem(itemIndex) {
    shoppingItems.splice(itemIndex, 1);
    localStorage.setItem('shoppingItems', shoppingItems);
}

function updateList() {
    document.getElementById('list').innerHTML = '';

    for (let index = 0; index < shoppingItems.length; index += 1) {
        document.getElementById('list').innerHTML += '<li>' + shoppingItems[index] +
            ' <button onclick="removeItem(' + index + '); updateList();">X</button></li>';
    }
}