import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import javax.swing.JFileChooser;
/**
 * @author Brandon Soledad
 */
public class ServiceOptions {
    
    ServiceOptions() {
        sOptions();
    }

    private void sOptions() {
        System.out.println("Choose one of the options below by inputing a number from 1-10 for the option respectively/n");
        System.out.println("Option 1:Compute a plain cryptographic hash of a given file.");
        System.out.println("Option 2:Compute a plain cryptographic hash of text input.");
        System.out.println("Option 3:Encrypt a given data file symmetrically under a given " +
            "password.");
        System.out.println("Option 4:Decrypt a given symmetric cryptogram under a given " +
            "password.");
        System.out.println("Option 5:Compute an authentication tag (MAC) of a given file " + 
        	"under a given password.");
        System.out.println("Option 6:Generate an elliptic key pair from a given password and " + 
        		"write the public key to a file.");
        System.out.println("Option 7:Encrypt a data file under a given elliptic public key file.");
        System.out.println("Option 8:Decrypt a given elliptic-encrypted file from a given password.");
        System.out.println("Option 9:Sign a given file from a given password and write the " + 
        		"signature to a file.");
        System.out.println("Option 10:Verify a given data file and its signature file under a given " + 
        		"public key file.");
        System.out.print("Select an option by inputting 1-10: ");
        Scanner sc = new Scanner(System.in);
        int option = sc.nextInt(); 
        sc.nextLine();
        if (option == 1) {
            option1();
        } else if (option == 2) {
            option2(sc);
        } else if (option == 3) {
            option3(sc);
        } else if (option == 4) {
            option4(sc);
        } else if (option == 5) {
        	option5(sc);
        } else if (option == 6) {
        	option6(sc);
        } else if (option == 7) {
        	option7();
        } else if (option == 8) {
        	option8(sc);
        } else if (option == 9) {
        	option9(sc);
        } else {
        	option10();
        }
    }
    
    public static void option1() {
    	System.out.println("Choose file: ");
    	final JFileChooser fc = new JFileChooser();
        int value = fc.showOpenDialog(null);

        if (value == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            try {
                byte[] x = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                String y = SymmetricCryptography.computeCryptographicHash(x);
                System.out.print("Plain Cryptographic Hash: " + y);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void option2(Scanner sc) {
    	System.out.print("Enter text: ");
        String x = sc.nextLine();
        String y = SymmetricCryptography.computeCryptographicHash(x.getBytes());
        System.out.println("Plain Cryptographic Hash: " + y);
    }
    
    public static void option3(Scanner sc) {
    	System.out.println("Choose data file: ");
    	final JFileChooser fc = new JFileChooser();
        int value = fc.showOpenDialog(null);

        if (value == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            try {
                byte[] x = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                System.out.print("Enter password: ");
                String password = sc.nextLine();
                byte[] cryptogram = SymmetricCryptography.encryptDataFile(x, password.getBytes());
                OutputStream os = new FileOutputStream(file);
                os.write(cryptogram);
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void option4(Scanner sc) {
    	System.out.println("Choose symmetric cryptogram: ");
    	final JFileChooser fc = new JFileChooser();
        int value = fc.showOpenDialog(null);

        if (value == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            try {
                byte[] cryptogram = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                System.out.print("Enter password: ");
                String password = sc.nextLine();
                byte[] x = SymmetricCryptography.decryptCryptogram(cryptogram, password.getBytes());
                OutputStream os = new FileOutputStream(file);
                os.write(x);
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void option5(Scanner sc) {
    	System.out.println("Choose file: ");
    	final JFileChooser fc = new JFileChooser();
        int value = fc.showOpenDialog(null);

        if (value == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            try {
            	byte[] x = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
            	System.out.print("Enter password: ");
                String password = sc.nextLine();
                String tag = SymmetricCryptography.computeAuthenticationTag(x, password.getBytes());
                System.out.println(tag);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void option6(Scanner sc) {
    	System.out.print("Enter password: ");
        String password = sc.nextLine();
        KeyPair kpair = ECC.generateEllipticKeyPair(password.getBytes());
        CurvePoint point = kpair.getP();
        System.out.print("Enter file name: ");
        String fileName = sc.nextLine();
        File file = new File(fileName + ".public_key");

        try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}   

        FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(point);
			System.out.println("Complete");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static void option7() {
    	System.out.println("Choose data file: ");
    	final JFileChooser fc = new JFileChooser();
        int value = fc.showOpenDialog(null);

        if (value == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            try {
                byte[] x = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                CurvePoint cPoint = null;
                System.out.println("Choose public key file: ");
                value = fc.showOpenDialog(null);

                if (value == JFileChooser.APPROVE_OPTION) {
                	File file2 = fc.getSelectedFile();
                	try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file2))) {
                		cPoint = (CurvePoint) ois.readObject();
                	} catch (Exception e) {
                		e.printStackTrace();
                	}
                }

                Cryptogram crypt = ECC.encryptDataFile(x, cPoint);
                FileOutputStream fout = null;
        		ObjectOutputStream oos = null;

        		try {
        			fout = new FileOutputStream(file);
        			oos = new ObjectOutputStream(fout);
        			oos.writeObject(crypt);
        			System.out.println("Complete");
        		} catch (Exception ex) {
        			ex.printStackTrace();
        		}
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void option8(Scanner sc) {
    	System.out.println("Choose elliptic-encrypted file: ");
    	final JFileChooser fc = new JFileChooser();
        int value = fc.showOpenDialog(null);

        if (value == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
        		Cryptogram crypt = (Cryptogram) ois.readObject();
        		System.out.print("Enter password: ");
                String password = sc.nextLine();
        		byte[] x = ECC.decryptCryptogram(crypt, password.getBytes());
        		OutputStream os = new FileOutputStream(file);
                os.write(x);
                os.close();
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }
    }
    
    public static void option9(Scanner in) {
    	System.out.print("Choose file: ");
    	final JFileChooser fc = new JFileChooser();
        int value = fc.showOpenDialog(null);

        if (value == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            try {
                byte[] x = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                System.out.print("Enter password: ");
                String password = in.nextLine();
                Signature sig = ECC.generateSignature(x, password.getBytes());
                System.out.print("Enter file name: ");
                String fileName = in.nextLine();
                File file2 = new File(fileName + ".signature");
                try {
        			file2.createNewFile();
        		} catch (IOException e) {
        			e.printStackTrace();
        		}     

                FileOutputStream fos = null;
        		ObjectOutputStream oos = null;

        		try {
        			fos = new FileOutputStream(file2);
        			oos = new ObjectOutputStream(fos);
        			oos.writeObject(sig);
        			System.out.println("Complete");
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
    }
    
    public static void option10() {
    	System.out.println("Choose data file: ");
    	final JFileChooser fc = new JFileChooser();
        int value = fc.showOpenDialog(null);

        if (value == JFileChooser.APPROVE_OPTION) {
        	File file = fc.getSelectedFile();

        	try {
                byte[] x = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                System.out.println("Choose signature file: ");
                value = fc.showOpenDialog(null);
                File file2 = null;
                Signature sig = null;

                if (value == JFileChooser.APPROVE_OPTION) {
                	file2 = fc.getSelectedFile();
                	try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file2))) {
                		sig = (Signature) ois.readObject();
                	} catch (Exception e) {
                		e.printStackTrace();
                	}
                }

                System.out.println("Select public key file: ");
                value = fc.showOpenDialog(null);
                CurvePoint cPoint = null;
                
                if (value == JFileChooser.APPROVE_OPTION) {
                	File file3 = fc.getSelectedFile();
                	try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file3))) {
                		cPoint = (CurvePoint) ois.readObject();
                	} catch (Exception e) {
                		e.printStackTrace();
                	}
                }
                System.out.println(ECC.verifySignature(sig, x, cPoint));
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
        }
    }
}
