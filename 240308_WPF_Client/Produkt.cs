using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;

namespace _240308_WPF_Client
{
    //INotifyPropertyChanged Interface implementieren, für Änderungen auf der Oberfläche 
    class Produkt : INotifyPropertyChanged
    {
        public string id { get; set; }
        public bool status { get; set; }
        public string produkt {  get; set; }

        public bool IsChecked
        {
            get { return status; }
            set
            {
                if (status != value)
                {
                    //Bei Änderung wird die Oberfläche aktualisiert
                    status = value;
                    OnPropertyChanged(nameof(IsChecked));
                }
            }
        }

        public string ProductName
        {
            get { return produkt; }
            set
            {
                if (produkt != value)
                {
                    //Bei Änderung wird die Oberfläche aktualisiert
                    produkt = value;
                    OnPropertyChanged(nameof(ProductName));
                }
            }
        }

        public event PropertyChangedEventHandler PropertyChanged;

        protected void OnPropertyChanged(string propertyName)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }

    }
}
