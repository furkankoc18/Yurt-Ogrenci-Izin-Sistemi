package Memur;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.Part;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import ogrenci.Ogrenci_Bilgileri;
import ogrenci._Izinler;

@ManagedBean
@SessionScoped
public class Ogrenci_Isleri implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Configuration configuration=new Configuration().configure("ogrenci/hibernate.cfg.xml");
	private SessionFactory factory=configuration.buildSessionFactory();
	private Session session=factory.openSession();
	private Transaction transaction=session.getTransaction();
	
	private Ogrenci_Bilgileri ogrenci_Bilgileri=new Ogrenci_Bilgileri();
	private List<_Izinler>gelenTaleplerListesi=new ArrayList<_Izinler>();	
	private Part file;
	
	
	
	//-------------------------------------------------------------------------------------------------
	
	public void yeniOgrenciKayit()
	{  
		Query sorgu=session.createQuery("From Ogrenci_Bilgileri o where o.tcNo= :tc or (o.isim = :isim and o.soyisim = :soyisim and o.adres = :adres)");
			sorgu.setLong("tc", ogrenci_Bilgileri.getTcNo());
			sorgu.setString("isim", ogrenci_Bilgileri.getIsim());
			sorgu.setString("soyisim", ogrenci_Bilgileri.getSoyisim());
			sorgu.setString("adres", ogrenci_Bilgileri.getAdres());
		if(sorgu.getResultList().isEmpty())
		{
			String filename="";
			for(String cd:file.getHeader("content-disposition").split(";"))
	            if(cd.trim().startsWith("filename"))
	            {	
	                 filename=cd.substring(cd.indexOf('=')+1).trim().replace("\"", "");
	                ogrenci_Bilgileri.setFotoDosyaYolu(filename);
	            }
	        try 
	        {
				file.write("C:\\Users\\Furkan\\Documents\\Eclipse Projeler\\Ogrenci_izin_sistemi\\WebContent\\resources\\img\\"+filename);			
	        }
	        catch (IOException e) 
	        {
				System.out.println("Dosya Yüklemede Hata Var : +"+filename+" =>"+e.toString());
			}
			System.out.println(" Öðrenci Kayýda Girdi");
			transaction.begin();
				session.save(ogrenci_Bilgileri);
			transaction.commit();
			session.clear();
			//session.close();
		}
		else 
		{
			System.out.println("Böyle Bir Öðrenci Mevcut TC=>"+ogrenci_Bilgileri.getTcNo());
		}
		
		
	}
	
	public void gelenIzinTalepler()
	{
		try 
		{

			Query gelentalep=session.createQuery("From _Izinler i where i.onay_durumu='Onaylanmadi'");
			gelenTaleplerListesi=gelentalep.getResultList();	
		} 
		catch (Exception e) 
		{
			System.out.println("Gelen Ýzin Taleplerini Çekmede Hata Var => "+e.toString());
		}
		
	}
	
	public void izinOnayla(_Izinler izinler)
	{
		System.out.println("Ýzinler => "+izinler.getIzin_baslangic_tarih());
		izinler.setOnay_durumu("Onaylandi");
		transaction.begin();
			session.update(izinler);
		transaction.commit();
	}
	public void izinIptal(_Izinler izinler)
	{
		System.out.println("Ýzinler2 => "+izinler.getIzin_telefon());
		izinler.setOnay_durumu("Rededildi");
		transaction.begin();
			session.update(izinler);
		transaction.commit();
	}
	
	
	
	
	//-----------------------------------------------------------------------------------
	
	
	
	
	public Ogrenci_Bilgileri getOgrenci_Bilgileri() {
		return ogrenci_Bilgileri;
	}
	public List<_Izinler> getGelenTaleplerListesi() {
		gelenIzinTalepler();
		return gelenTaleplerListesi;
	}
	public void setGelenTaleplerListesi(List<_Izinler> gelenTaleplerListesi) {
		this.gelenTaleplerListesi = gelenTaleplerListesi;
	}
	public void setOgrenci_Bilgileri(Ogrenci_Bilgileri ogrenci_Bilgileri) {
		this.ogrenci_Bilgileri = ogrenci_Bilgileri;
	}
	public Part getFile() {
		return file;
	}
	public void setFile(Part file) {
		this.file = file;
	}
}
