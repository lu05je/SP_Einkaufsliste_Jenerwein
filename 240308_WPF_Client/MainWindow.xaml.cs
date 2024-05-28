﻿using System.Collections.ObjectModel;
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

namespace _240308_WPF_Client
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private ObservableCollection<Produkt> produkte;

        public MainWindow()
        {
            InitializeComponent();
            produkte = new ObservableCollection<Produkt>();
            produktListBox.ItemsSource = produkte;
        }

        private void AddButton_Click(object sender, RoutedEventArgs e)
        {
            string produktName = produktTextBox.Text.Trim();
            if (!string.IsNullOrEmpty(produktName))
            {
                Produkt neuesProdukt = new Produkt { produkt = produktName };
                SaveProdukt(neuesProdukt, (error) => {
                    if (error == null)
                    {
                        Dispatcher.Invoke(() => produkte.Add(neuesProdukt));
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
                HttpClient client = new HttpClient();
                string json = JsonSerializer.Serialize(produkt);
                StringContent content = new StringContent(json, Encoding.UTF8, "application/json");
                HttpResponseMessage response = await client.PostAsync("http://10.10.2.79:8080/api/produkt", content);

                if (response.IsSuccessStatusCode)
                {
                    callback(null);
                }
                else
                {
                    callback(response.ReasonPhrase);
                }
            }
            catch (Exception ex)
            {
                callback(ex.Message);
            }
        }
    }
}