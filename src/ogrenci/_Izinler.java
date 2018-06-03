package ogrenci;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="izinler")
public class _Izinler 
{
	private long id;
	private Date izin_baslangic_tarih;
	private Date izin_bitis_tarih;
	private String izin_adres;
	private long izin_telefon;
	private String onay_durumu;
	
	//-------------------------------------------------------------------------------------
	
	public _Izinler(Date izin_baslangic_tarih, Date izin_bitis_tarih, String izin_adres, long izin_telefon,
			String onay_durumu) 
	{
		this.izin_baslangic_tarih = izin_baslangic_tarih;
		this.izin_bitis_tarih = izin_bitis_tarih;
		this.izin_adres = izin_adres;
		this.izin_telefon = izin_telefon;
		this.onay_durumu = onay_durumu;
	}
	
	public _Izinler() {
		// TODO Auto-generated constructor stub
	}
	
	//-------------------------------------------------------------------------------------
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getIzin_baslangic_tarih() {
		return izin_baslangic_tarih;
	}
	public void setIzin_baslangic_tarih(Date izin_baslangic_tarih) {
		this.izin_baslangic_tarih = izin_baslangic_tarih;
	}
	public Date getIzin_bitis_tarih() {
//		SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
	//	dateFormat.format(izin_bitis_tarih);
		return izin_bitis_tarih;
	}
	public void setIzin_bitis_tarih(Date izin_bitis_tarih) {
		this.izin_bitis_tarih = izin_bitis_tarih;
	}
	public String getIzin_adres() {
		return izin_adres;
	}
	public void setIzin_adres(String izin_adres) {
		this.izin_adres = izin_adres;
	}
	public long getIzin_telefon() {
		return izin_telefon;
	}
	public void setIzin_telefon(long izin_telefon) {
		this.izin_telefon = izin_telefon;
	}
	public String getOnay_durumu() {
		return onay_durumu;
	}
	public void setOnay_durumu(String onay_durumu) {
		this.onay_durumu = onay_durumu;
	}	
}
