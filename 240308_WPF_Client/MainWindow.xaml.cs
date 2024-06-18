using System.Collections.ObjectModel;
using System.Net.Http;
using System.Text;
using System.Text.Json;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using static System.Runtime.InteropServices.JavaScript.JSType;

namespace _240308_WPF_Client
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        //List<Produkt> produkts = new List<Produkt>();
        private ObservableCollection<Produkt> produkts = new ObservableCollection<Produkt>();

        public MainWindow()
        {
            InitializeComponent();

            //Produkte aus der Datenbank laden
            LoadProdukte();

            //Quelle für die Itemlist festlegen
            ItemsList.ItemsSource = produkts;
        }

        private async void LoadProdukte()
        {
            try
            {
                //alle Produkte aus der Datenbank lesen
                HttpClient client = new HttpClient();
                HttpResponseMessage response = await client.GetAsync("http://localhost:8080/api/produkte");
                if (response.IsSuccessStatusCode)
                {
                    //in Observable Collection speichern
                    string json = await response.Content.ReadAsStringAsync();
                    var produktList = JsonSerializer.Deserialize<ObservableCollection<Produkt>>(json);
                    foreach (var produkt in produktList)
                    {
                        produkts.Add(produkt);
                    }
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Fehler beim Laden der Produkte: {ex.Message}");
            }
        }

        private void AddButton_Click(object sender, RoutedEventArgs e)
        {
            //neues Produkt erstellen wenn Text nicht leer ist
            string produktName = produktTextBox.Text.Trim();
            if (!string.IsNullOrEmpty(produktName))
            {
                Produkt newProdukt = new Produkt { produkt = produktName, status = false };
                //Save Funktion aufrufen
                SaveProdukt(newProdukt, (error) => {
                    if (error == null)
                    {
                        //Text zurücksetzen
                        produktTextBox.Text = "";
                    }
                    else
                    {
                        MessageBox.Show($"Fehler beim Speichern des Produkts: {error}");
                    }
                });
            }
        }

        private async void SaveProdukt(Produkt produkt, Action<string> callback)
        {
            try
            {
                //neues Produkt zur Datenbank hinzufügen
                HttpClient client = new HttpClient();
                string json = JsonSerializer.Serialize(produkt);
                StringContent content = new StringContent(json, Encoding.UTF8, "application/json");
                HttpResponseMessage response = await client.PostAsync("http://localhost:8080/api/produkt", content);

                if (response.IsSuccessStatusCode)
                {
                    //Produkte neu aus der Datenbank lesen, um neue ID zu bekommen
                    produkts.Clear();
                    LoadProdukte();

                    callback(null);     //wenn erfolgreich -> null zurückgeben
                }
                else
                {
                    callback(response.ReasonPhrase);    //ansonsten Error
                }
            }
            catch (Exception ex)
            {
                callback(ex.Message);
            }
        }

        private void EditButton_Click(object sender, RoutedEventArgs e)
        {
            //Welches Element wurde geändert
            var button = sender as Button;
            var produkt = button?.Tag as Produkt;
            if (produkt != null)
            {
                //Dialog öffnen
                var inputDialog = new EditDialog(produkt.ProductName);
                if (inputDialog.ShowDialog() == true)
                {
                    //Attribut ändern und PUT-Funktion aufrufen
                    produkt.ProductName = inputDialog.EditedText;
                    EditProduktItem(produkt);
                }
            }
        }

        private void CheckBox_Click(object sender, RoutedEventArgs e)
        {
            //Welches Element wurde geändert
            var checkBox = sender as CheckBox;
            var produkt = checkBox?.DataContext as Produkt;
            if (produkt != null)
            {
                //Attribut ändern und PUT-Funktion aufrufen
                produkt.status = checkBox.IsChecked ?? false;
                EditProduktItem(produkt);
            }
        }

        private async void DeleteButton_Click(object sender, RoutedEventArgs e)
        {
            //Welches Element soll gelöscht werden
            var button = sender as Button;
            var produkt = button?.Tag as Produkt;
            if (produkt != null)
            {
                try
                {
                    using (var client = new HttpClient())
                    {
                        //Produkt mittels ID aus der Datenbank löschen
                        HttpResponseMessage response = await client.DeleteAsync($"http://localhost:8080/api/produkt/{produkt.id}");
                        if (!response.IsSuccessStatusCode)
                        {
                            throw new Exception("Fehler beim Löschen des Produkts");
                        }
                        else
                        {
                            //aus Observable Collection entfernen
                            produkts.Remove(produkt);
                        }
                    }
                }
                catch (Exception ex)
                {
                    MessageBox.Show($"Fehler beim Löschen des Produkts: {ex.Message}");
                    throw;
                }  
            }
        }

        private async void EditProduktItem(Produkt p)
        {
            try
            {
                // Produkt aktualisieren
                HttpClient client = new HttpClient();
                string json = JsonSerializer.Serialize(p);
                StringContent content = new StringContent(json, Encoding.UTF8, "application/json");
                HttpResponseMessage response = await client.PutAsync("http://localhost:8080/api/produkt", content);
                if (!response.IsSuccessStatusCode)
                {
                    throw new Exception("Fehler beim Löschen des Produkts");
                }

            }
            catch (Exception ex)
            {
                MessageBox.Show($"Fehler beim Aktualisieren des Produkts: {ex.Message}");
            }
        }

        private void refreshButton_Click(object sender, RoutedEventArgs e)
        {
            produkts.Clear();
            LoadProdukte();
        }
    }
}