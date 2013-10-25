package chapter03.autoinstaller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class AutoInstaller extends Thread {
	BundleContext context;
	boolean working = true;
	
	int delay = 2000;	
	File jardir;
	
	public AutoInstaller(BundleContext context) {
		super();
		this.context = context;
		
		jardir = new File("./bundles");
		jardir.mkdirs();
	}

	public void close()
	{		
		working = false;
		interrupt();
		try {
			join(10000);
		} catch (InterruptedException ie) {
			
		}
	}


	public void run() {
		System.out.println("Starting AutoInstaller Worker");
		while (working)
			try {
				// ����Ž��
				Map<String,File> bundlesInDir = getBundlesInDir(jardir);
				
				// ���ϼ�ġ
				installBundles(bundlesInDir, jardir);
				
				Thread.sleep(delay);
			} catch (InterruptedException e) {

			} 
		System.out.println("Quitting AutoInstaller Worker");
	}
	
	
	private void installBundles(Map<String, File> bundlesInDir, File dir) {
		Bundle bundles[] = context.getBundles();
		
		for (Bundle bundle : bundles) 
		{			
			String location = bundle.getLocation();
			File file = (File) bundlesInDir.get(location);			
			if (file != null) { //��ġ������ �ִ� �����̸�				
				if (file.lastModified() > bundle.getLastModified()) // ���� ��ġ�Ŀ� ����Ǿ��ٸ�
				{					
					try {
						InputStream in;
						in = new FileInputStream(file);						
						bundle.update(in);											
						bundle.start();
						in.close();
						System.out.println("���� ������Ʈ : " + location);
					} catch (Exception e) {
						System.out.println("���� ��ġ���� : " + e);
					}					
				} else if (bundle.getState() != Bundle.ACTIVE)
					try {
						bundle.start();
					} catch (BundleException e) {
						System.out.println("���� ���۽��� : " + e);						
					}
				
				bundlesInDir.remove(location); // �� ������ ó���� �Ǿ����Ƿ� ����Ʈ���� �����.
			} else {
				if (bundle.getLocation().startsWith(dir.getAbsolutePath())) {
					try {
						bundle.uninstall();						
						System.out.println("���� �����Ϸ� : " + location);
					} catch (Exception e) {
						System.out.println("���� ��ġ���� : " + e);
					}
				}
			}			
		}
		
		for(File file : bundlesInDir.values())
		{			
			try {
				InputStream in = new FileInputStream(file);
				Bundle bundle = context.installBundle(file.getAbsolutePath(),in);		
				in.close();			
				bundle.start();
				System.out.println("���� ��ġ�Ϸ� : " + file.getAbsolutePath());	
			} catch (Exception e) {
				System.out.println("���� ��ġ���� : " + e);				
			}
		}	
	}

	private Map<String,File> getBundlesInDir(File dir) {
		Map<String,File> bundles = new HashMap<String,File>();
		String filelist[] = dir.list();
		
		for (String jarfile : filelist) {			
			File file = new File(dir, jarfile);
			if (jarfile.endsWith(".jar")) {
				bundles.put(file.getAbsolutePath(), file);
			}
		}	
		
		return bundles;
	}
	
	
	

}
