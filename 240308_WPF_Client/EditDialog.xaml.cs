using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace _240308_WPF_Client
{
    /// <summary>
    /// Interaktionslogik für EditDialog.xaml
    /// </summary>
    public partial class EditDialog : Window
    {
        public string EditedText { get; set; }
        public EditDialog(string produktText)
        {
            InitializeComponent();
            editProduktTextBox.Text = produktText;  //Text des momentanen Produkt setzen
        }

        private void saveButton_Click(object sender, RoutedEventArgs e)
        {
            //Nach Klick Text übernehmen und Dialog schliessen
            this.EditedText = editProduktTextBox.Text;
            this.DialogResult = true;
            this.Close();
        }
    }
}
