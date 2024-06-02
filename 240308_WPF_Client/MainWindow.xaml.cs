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
        ObservableCollection<Produkt> produkts = new ObservableCollection<Produkt>();

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
                HttpResponseMessage response = await client.GetAsync("http://192.168.0.42:8080/api/produkte");
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
                        Dispatcher.Invoke(() => produkts.Add(newProdukt));
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
                HttpResponseMessage response = await client.PostAsync("http://192.168.0.42:8080/api/produkt", content);

                if (response.IsSuccessStatusCode)
                {
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

        }

        private void DeleteButton_Click(object sender, RoutedEventArgs e)
        {

        }
    }
}