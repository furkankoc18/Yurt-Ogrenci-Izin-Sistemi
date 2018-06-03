package ogrenci;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;import javax.persistence.Table;
import javax.servlet.http.Part;

import org.hibernate.annotations.Cascade;

@Entity
@Table(name="Ogrenci_Bilgileri")
public class Ogrenci_Bilgileri implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private long tcNo;
	private String isim;
	private String soyisim;
	private long telefonNo;
	private String parola;
	private String adres;
	private List<_Izinler>izinlerListe=new ArrayList<_Izinler>();
	private String fotoDosyaYolu;
	
	
	//------------------------------------------------------------------------------



	public Ogrenci_Bilgileri(long tcNo, String isim, String soyisim, long telefonNo, String parola, String adres) 
	{
		this.tcNo = tcNo;
		this.isim = isim;
		this.soyisim = soyisim;
		this.telefonNo = telefonNo;
		this.parola = parola;
		this.adres = adres;
	}
		
	public Ogrenci_Bilgileri() {
		// TODO Auto-generated constructor stub
	}
	
	//--------------------------------------------------------------------------------
	
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public long getTcNo() {
		return tcNo;
	}
	public void setTcNo(long tcNo) {
		this.tcNo = tcNo;
	}
	@OneToMany(cascade=CascadeType.ALL)
	@JoinTable(
			name="izinlerListesi",
			joinColumns=@JoinColumn(name="tc",referencedColumnName="TcNo"),
			inverseJoinColumns=@JoinColumn(referencedColumnName="id",name="izin_id")
			)
	public List<_Izinler> getIzinlerListe() {
		return izinlerListe;
	}
	public void setIzinlerListe(List<_Izinler> izinlerListe) {
		this.izinlerListe = izinlerListe;
	}
	
	//---------------------------------------------------------------------------------
	
	public String getIsim() {
		return isim;
	}
	public void setIsim(String isim) {
		this.isim = isim;
	}
	public String getSoyisim() {
		return soyisim;
	}
	public void setSoyisim(String soyisim) {
		this.soyisim = soyisim;
	}
	public long getTelefonNo() {
		return telefonNo;
	}
	public void setTelefonNo(long telefonNo) {
		this.telefonNo = telefonNo;
	}
	public String getParola() {
		return parola;
	}
	public void setParola(String parola) {
		this.parola = parola;
	}
	public String getAdres() {
		return adres;
	}
	public void setAdres(String adres) {
		this.adres = adres;
	}
	public String getFotoDosyaYolu() {
		return fotoDosyaYolu;
	}
	public void setFotoDosyaYolu(String fotoDosyaYolu) {
		this.fotoDosyaYolu = fotoDosyaYolu;
	}
}
