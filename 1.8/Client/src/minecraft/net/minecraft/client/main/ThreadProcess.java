package net.minecraft.client.main;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;


import net.minecraft.client.Minecraft;

import org.lwjgl.input.Keyboard;

public class ThreadProcess extends Thread
{
  protected Minecraft applet;
  public HashSet cheatinterdit = new HashSet(32);
  private static final Pattern COMPILE = Pattern.compile(",");
  private String author = "This code is prioprity of SoftHack alias Wincode";

  public ThreadProcess(Minecraft client)
  {
    this.applet = client;
    this.cheatinterdit.add("\"cheatengine-i386.exe\"");
    this.cheatinterdit.add("\"cheatengine-x86_64.exe\"");
    this.cheatinterdit.add("\"Cheat Engine.exe\"");
    this.cheatinterdit.add("\"AutoClick.exe\"");
    this.cheatinterdit.add("\"SuperRapidFire.exe\"");
    this.cheatinterdit.add("\"Cheat Engine 6.1.exe\"");
  }

  public void run() 
  	
  	{
    while (!this.applet.isGamePaused()) {
      if (checkProcess()) {
        new JOptionPane(); JOptionPane.showMessageDialog(new Frame(), "La triche est interdite sur Alkazia ! Veuillez désactiver votre logiciel de triche pour continuer � jouer !", "Alkazia", 0);
        applet.shutdown();
      	System.exit(0);
      }
      
      
    }

    try
    {
      Thread.sleep(10000L);
    }
    catch (InterruptedException ignored)
    {
    }
  }

  private boolean checkProcess() {
    InputStreamReader reader = null;
    BufferedReader buffer = null;
    try
    {
      Process process = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe /fo csv /nh");
      reader = new InputStreamReader(process.getInputStream());
      buffer = new BufferedReader(reader);
      String current;
      while ((current = buffer.readLine()) != null) {
        if (this.cheatinterdit.contains(COMPILE.split(current)[0])) {
          buffer.close();
          reader.close();
          return true;
        }
      }

      buffer.close();
      reader.close();
    } catch (IOException ignored) {
    } finally {
      try {
        if (buffer != null) buffer.close();
        if (reader != null) reader.close();
      }
      catch (IOException ignored)
      {
      }
    }
    return false;
  }
}