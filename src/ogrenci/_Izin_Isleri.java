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
	private static final long serialVersionUID = 1L;//Serializable default deðeri
	
	private Configuration configuration=new Configuration().configure("ogrenci/hibernate.cfg.xml"); //Hibernate cfg. dosyasý için
	private SessionFactory factory=configuration.buildSessionFactory();//SessionFactory Nesnesi Ürettim
	private Session session=factory.openSession();//Session Nesnesi Ürettim
	private Transaction transaction=session.getTransaction();//Transaction nesnesi üretip,session nesnesindeki transactionu atadým
	
	private Ogrenci_Bilgileri ogrenciBilgileri=new Ogrenci_Bilgileri();//Xhtml sayfasýnda managedbean olarak kullandým ve verileri yükledim
	private _Izinler izinler=new _Izinler();//Xhtml sayfasýnda manageed bean olarak alt nesne olarak kullandým ve verileri yükledim
	private List<_Izinler>gecmisIzinler=new ArrayList<_Izinler>();//izinleriCek() methodunu kullanarak geçmiþ izinleri çektim ve bu listeye atadým.Datatableda kullandým

	private boolean gorunurluk=false;
	
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	
	public boolean isGorunurluk() {
		return gorunurluk;
	}
	public void setGorunurluk(boolean gorunurluk) {
		this.gorunurluk = gorunurluk;
	}
	public  String sistemeGiris()//Öðrencinin sisteme giriþ yapmasý için kullandým ve action olarak butona verdim
	{
		Query <Ogrenci_Bilgileri>query=session.createQuery("from Ogrenci_Bilgileri o where o.tcNo= :tc and o.parola =:parola");//Eðer girilen tc ve þifre varsa diye hql kodu yazdým
		query.setLong("tc", ogrenciBilgileri.getTcNo());//tc deðerine,sisteme giriþ yapýlmaya çalýþýlan tcyi atýyorum
		query.setString("parola", ogrenciBilgileri.getParola());//parola deðerine,sisteme giriþ yapýlmaya çalýþýlan parolayý atýyorum
		
		if(!query.list().isEmpty())//Eðer parola yada tc yanlýþ girildiyse veri çekilmiyor ve liste boþ oluyor. Eðer boþ deðilse
		{
			System.out.println("Sisteme Giriþte Kullanýcý Bulundu Ýsim -> "+query.getResultList().get(0).getIsim());//Sisteme giriþ yapan öðrencinin ismi console'a yazdýrýyorum
			ogrenciBilgileri=query.getSingleResult();//ogrencibilgileri nesnesine sisteme giriþ yapýlan öðrencinin bilgilerini atýyorum
			return "index.xhtml?faces-redirect=true";//Eðer herþey doðru ise index.xhtml sayfasýna yönlendirme yapýlýyor
		}
		else//Eðer giriþ baþarýsýz olursa ayný sayfada kalýyor
			return "";
	}
	public void izinTalepEt()//Öðrenci izin talep iþlemi bu methodta yapýlýyor
	{
		Date date=new Date();
        FacesContext context = FacesContext.getCurrentInstance();

        
        
		
		if(izinler.getIzin_baslangic_tarih().after(izinler.getIzin_bitis_tarih()))
		{
			System.out.println(" Hata Baþlangýç Tarihi Bitiþ Tarihinden Büyük");
			gorunurluk=true;
		     //FacesContext.getCurrentInstance().getExternalContext().invalidateSession();  // Sessin Komple Temizliyor Ve Kapatýyor
 
		}
		else
		{
			try 
			{
				_Izinler _izinler =new _Izinler(izinler.getIzin_baslangic_tarih(), izinler.getIzin_bitis_tarih(), //her izin için için izinler nesnesi üretiliyor ve izinler managedbeanýndaki bilgiler atanýyor
						izinler.getIzin_adres(),izinler.getIzin_telefon(), "Onaylanmadi");
				System.err.println("Session Açýk mý : "+session.isOpen());//session açýk mý kontrol ediyor ve console yazdýrýyor
				ogrenciBilgileri.getIzinlerListe().add(_izinler);//izni onetomany iliþkisi ile ogrencibilgilerine ekleniyor
				
				System.out.println("Gidilen Ýzin Baþlangýç Tarihi => "+izinler.getIzin_baslangic_tarih());
				System.out.println("Gidilen Ýzin Bitiþ Tarihi => "+izinler.getIzin_bitis_tarih());
				
				transaction.begin();//transaction baþlatýlýyor
					session.save(ogrenciBilgileri);//Öðrenciye ait izinler tablolara ekleniyor
				transaction.commit();//transaction iþleniyor
			
			} 
			catch (Exception e)//Hata aldýðýmýzda 
			{
				System.out.println("Hata Burada -> "+e.toString());
			}
				System.err.println("-----------------------------------------------------------------");
		}
	}
	public String sistemdenCikis()//Öðrenci Sistemden çýkýþ yaptýðýnda
	{
		//FacesContext.getCurrentInstance().getExternalContext().invalidateSession();	
		session.clear();//Sistemden çýkýþ yapýldýðýnda session boþaltýlýyor ve temizleniyor
		return "giris.xhtml?faces-redirect=true";//çýkýþa basýldýðýnda giris.xhtml sayfasýnda yönlendirme iþlemi yapýlýyor
	}
	public void tumizinleriCek()//Sisteme giriþ yapan öðrencinin geçmiþ izinlerini çekiyor //NOT=> SQL Native de addEntity kullan yoksa hata veriyor
	{
			Query izinlerListesi=session.createNativeQuery("select * from izinler where id in"
				+ " (select izin_id from izinlerlistesi where tc="+ogrenciBilgileri.getTcNo()+")" ).addEntity(_Izinler.class);//Sistemdeki öðrencinin izin bilgilerini çekiyor
			
		gecmisIzinler=izinlerListesi.list();//gecmisizinler listesine sql kodundan gelen liste atanýyor
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
		System.out.println("CÜMLE => "+diger);
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
	//	tumizinleriCek();//gecmisizinler.xhtml sayfasý açýldýðýnda veriler çekilmesi için bu methodu çaðýrdým
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
