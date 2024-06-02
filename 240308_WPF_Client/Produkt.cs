using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace _240308_WPF_Client
{
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
