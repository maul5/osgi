package chapter07.gameserver;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.PreferencesService;
import org.osgi.service.prefs.Preferences;

public class Activator implements BundleActivator {

	private ServiceTracker tracker;
	private PreferencesService service;

	public void start(BundleContext context) throws Exception {
		tracker = new ServiceTracker(context, PreferencesService.class.getName(), null);
		tracker.open();		
		service = (PreferencesService) tracker.getService();
		
		// �ý��� ��Ʈ Preferences �� �����´�.
		Preferences prefs = service.getSystemPreferences();				
		prefs.put("ServerName", "Gul'dan");
		
		// ������带 �ѹ��� �����Ѵ�. 
		prefs = prefs.node("Guild/Heroes");		
		prefs.put("Master","Chris");
		
		 
		// ���� ��Ʈ Preferences �� �����´�.
		prefs = service.getUserPreferences("");
		prefs.putInt("DefaultLife",12000);
		
		
		// ���� Chris �� ��Ʈ Preferences �� �����´�.
		prefs = service.getUserPreferences("Chris");
		prefs = prefs.node("Character1");
		prefs.put("Name","Warrior");
		prefs.putInt("Life",18900);
		
		prefs = prefs.node("Item");
		prefs = prefs.node("Knife");
		prefs.putInt("Max Damage",353);
		prefs.putInt("Min Damage",235);
		prefs.putFloat("Damage per Second", 163.3F);
		
		prefs = prefs.node("/Character1/Talents");
		prefs.putInt("Combat", 25);
		prefs.putInt("Assassination", 20);
				
		try {
			service.getSystemPreferences().flush();
			service.getUserPreferences("").flush();
		} catch (BackingStoreException e) {			
			e.printStackTrace();
		}	
		
		
		System.out.println("Printing System Preferences -----");
		printPreferences(service.getSystemPreferences());
		
		System.out.println("Printing User Preferences -----");
		printPreferences(service.getUserPreferences(""));
	}

	private void printPreferences(Preferences prefs) {
				
		try {			
			// �Ӽ����
			for (String key : prefs.keys() ) {
				System.out.println("[" + prefs.absolutePath() + " ] " + key + " : " + prefs.get(key, ""));			
			}
			
			// ��� ������� ���
			for (String child : prefs.childrenNames())
				printPreferences(prefs.node(child));
			
		} catch (BackingStoreException e) {			
			e.printStackTrace();
		}		
		
	}

	public void stop(BundleContext context) throws Exception {
		// clean up
		tracker.close();
		tracker = null;
		
		service = null;
	}

}
