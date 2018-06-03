package ogrenci;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;


@ManagedBean
@SessionScoped
public class _Izin_Isleri implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;//Serializable default de�eri
	
	private Configuration configuration=new Configuration().configure("ogrenci/hibernate.cfg.xml"); //Hibernate cfg. dosyas� i�in
	private SessionFactory factory=configuration.buildSessionFactory();//SessionFactory Nesnesi �rettim
	private Session session=factory.openSession();//Session Nesnesi �rettim
	private Transaction transaction=session.getTransaction();//Transaction nesnesi �retip,session nesnesindeki transactionu atad�m
	
	private Ogrenci_Bilgileri ogrenciBilgileri=new Ogrenci_Bilgileri();//Xhtml sayfas�nda managedbean olarak kulland�m ve verileri y�kledim
	private _Izinler izinler=new _Izinler();//Xhtml sayfas�nda manageed bean olarak alt nesne olarak kulland�m ve verileri y�kledim
	private List<_Izinler>gecmisIzinler=new ArrayList<_Izinler>();//izinleriCek() methodunu kullanarak ge�mi� izinleri �ektim ve bu listeye atad�m.Datatableda kulland�m

	private boolean gorunurluk=false;
	
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	
	public boolean isGorunurluk() {
		return gorunurluk;
	}
	public void setGorunurluk(boolean gorunurluk) {
		this.gorunurluk = gorunurluk;
	}
	public  String sistemeGiris()//��rencinin sisteme giri� yapmas� i�in kulland�m ve action olarak butona verdim
	{
		Query <Ogrenci_Bilgileri>query=session.createQuery("from Ogrenci_Bilgileri o where o.tcNo= :tc and o.parola =:parola");//E�er girilen tc ve �ifre varsa diye hql kodu yazd�m
		query.setLong("tc", ogrenciBilgileri.getTcNo());//tc de�erine,sisteme giri� yap�lmaya �al���lan tcyi at�yorum
		query.setString("parola", ogrenciBilgileri.getParola());//parola de�erine,sisteme giri� yap�lmaya �al���lan parolay� at�yorum
		
		if(!query.list().isEmpty())//E�er parola yada tc yanl�� girildiyse veri �ekilmiyor ve liste bo� oluyor. E�er bo� de�ilse
		{
			System.out.println("Sisteme Giri�te Kullan�c� Bulundu �sim -> "+query.getResultList().get(0).getIsim());//Sisteme giri� yapan ��rencinin ismi console'a yazd�r�yorum
			ogrenciBilgileri=query.getSingleResult();//ogrencibilgileri nesnesine sisteme giri� yap�lan ��rencinin bilgilerini at�yorum
			return "index.xhtml?faces-redirect=true";//E�er her�ey do�ru ise index.xhtml sayfas�na y�nlendirme yap�l�yor
		}
		else//E�er giri� ba�ar�s�z olursa ayn� sayfada kal�yor
			return "";
	}
	public void izinTalepEt()//��renci izin talep i�lemi bu methodta yap�l�yor
	{
		Date date=new Date();
        FacesContext context = FacesContext.getCurrentInstance();

        
        
		
		if(izinler.getIzin_baslangic_tarih().after(izinler.getIzin_bitis_tarih()))
		{
			System.out.println(" Hata Ba�lang�� Tarihi Biti� Tarihinden B�y�k");
			gorunurluk=true;
		     //FacesContext.getCurrentInstance().getExternalContext().invalidateSession();  // Sessin Komple Temizliyor Ve Kapat�yor
 
		}
		else
		{
			try 
			{
				_Izinler _izinler =new _Izinler(izinler.getIzin_baslangic_tarih(), izinler.getIzin_bitis_tarih(), //her izin i�in i�in izinler nesnesi �retiliyor ve izinler managedbean�ndaki bilgiler atan�yor
						izinler.getIzin_adres(),izinler.getIzin_telefon(), "Onaylanmadi");
				System.err.println("Session A��k m� : "+session.isOpen());//session a��k m� kontrol ediyor ve console yazd�r�yor
				ogrenciBilgileri.getIzinlerListe().add(_izinler);//izni onetomany ili�kisi ile ogrencibilgilerine ekleniyor
				
				System.out.println("Gidilen �zin Ba�lang�� Tarihi => "+izinler.getIzin_baslangic_tarih());
				System.out.println("Gidilen �zin Biti� Tarihi => "+izinler.getIzin_bitis_tarih());
				
				transaction.begin();//transaction ba�lat�l�yor
					session.save(ogrenciBilgileri);//��renciye ait izinler tablolara ekleniyor
				transaction.commit();//transaction i�leniyor
			
			} 
			catch (Exception e)//Hata ald���m�zda 
			{
				System.out.println("Hata Burada -> "+e.toString());
			}
				System.err.println("-----------------------------------------------------------------");
		}
	}
	public String sistemdenCikis()//��renci Sistemden ��k�� yapt���nda
	{
		//FacesContext.getCurrentInstance().getExternalContext().invalidateSession();	
		session.clear();//Sistemden ��k�� yap�ld���nda session bo�alt�l�yor ve temizleniyor
		return "giris.xhtml?faces-redirect=true";//��k��a bas�ld���nda giris.xhtml sayfas�nda y�nlendirme i�lemi yap�l�yor
	}
	public void tumizinleriCek()//Sisteme giri� yapan ��rencinin ge�mi� izinlerini �ekiyor //NOT=> SQL Native de addEntity kullan yoksa hata veriyor
	{
			Query izinlerListesi=session.createNativeQuery("select * from izinler where id in"
				+ " (select izin_id from izinlerlistesi where tc="+ogrenciBilgileri.getTcNo()+")" ).addEntity(_Izinler.class);//Sistemdeki ��rencinin izin bilgilerini �ekiyor
			
		gecmisIzinler=izinlerListesi.list();//gecmisizinler listesine sql kodundan gelen liste atan�yor
	}
	
	public void sonBesAdetIzinCek()
	{
		Query izinlerListesi=session.createNativeQuery("select * from izinler where id in"
				+ " (select izin_id from izinlerlistesi where tc="+ogrenciBilgileri.getTcNo()+") order by id desc limit 5" ).addEntity(_Izinler.class);
		gecmisIzinler=izinlerListesi.list();
	}
	
	
	public void bilgileriGuncelle()
	{
		transaction.begin();
			session.update(ogrenciBilgileri);
		transaction.commit();		
	}
	
	
	
/*	public void sayiYap()
	{
		String cumle="(0537) 898-7660";
		String diger;
		diger=cumle.substring(1, 5)+cumle.substring(7,10)+cumle.substring(11,15);
		System.out.println("C�MLE => "+diger);
		long a=Long.valueOf(diger);
	}
	
	*/
	
	/*
	public void temizle()
	{
		izinler.setIzin_baslangic_tarih(null);
		izinler.setIzin_telefon(0);
		izinler.setIzin_bitis_tarih(null);
		izinler.setIzin_adres("");
		gorunurluk=false;
	}
	*/
	
	
	
	//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public List<_Izinler> getGecmisIzinler() {
	//	tumizinleriCek();//gecmisizinler.xhtml sayfas� a��ld���nda veriler �ekilmesi i�in bu methodu �a��rd�m
		sonBesAdetIzinCek();
		return gecmisIzinler;
	}
	public void setGecmisIzinler(List<_Izinler> gecmisIzinler) {
		this.gecmisIzinler = gecmisIzinler;
	}
	public _Izinler getIzinler() {
		return izinler;
	}

	public void setIzinler(_Izinler izinler) {
		this.izinler = izinler;
	}

	public Ogrenci_Bilgileri getOgrenciBilgileri() {
		return ogrenciBilgileri;
	}

	public void setOgrenciBilgileri(Ogrenci_Bilgileri ogrenciBilgileri) {
		this.ogrenciBilgileri = ogrenciBilgileri;
	}
}
