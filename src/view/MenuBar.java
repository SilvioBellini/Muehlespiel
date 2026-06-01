package view;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This class creates the menu bar for the game, which includes a "Help" menu
 * and a "Credits" menu. The "Help" menu provides game rules, while the "Credits"
 * menu displays information about the contributors to the project.
 */
public class MenuBar {
    /**
     * Creates the "Help" menu of the application, which contains items providing
     * information or assistance, such as the game rules.
     *
     * @return The "Help" menu populated with relevant menu items.
     */
    public static JMenu createHelpMenu(JFrame window) {
        JMenu help = new JMenu("Hilfe");
        JMenuItem rules = new JMenuItem("Regeln");

        String rulesText =

                "<html><div style='text-align: center;'>" +
                        "<h3>1. Spielziel:</h3>" +
                        "Ziel ist es, alle gegnerischen Steine zu entfernen oder ihn zu blockieren,<br>" +
                        "sodass er keinen gültigen Zug mehr machen kann.<br><br>" +

                        "<h3>2. Spielphasen:</h3>" +
                        "<u>a) Setzphase:</u><br>" +
                        "Jeder Spieler setzt abwechselnd einen Stein auf ein freies Feld.<br>" +
                        "Wenn eine Mühle (3 Steine in einer Linie) entsteht, darf ein gegnerischer Stein entfernt werden.<br><br>"
                        +

                        "<u>b) Zugphase:</u><br>" +
                        "Nachdem alle Steine gesetzt wurden, bewegen die Spieler abwechselnd einen Stein<br>" +
                        "auf ein benachbartes Feld. Wieder gilt: bei einer Mühle darf ein gegnerischer Stein entfernt werden.<br><br>"
                        +

                        "<u>c) Springphase:</u><br>" +
                        "Wenn ein Spieler nur noch 3 Steine hat, darf er mit diesen auf ein beliebiges freies Feld springen.<br><br>"
                        +

                        "<h3>3. Mühle-Regel:</h3>" +
                        "Eine Mühle entsteht, wenn 3 eigene Steine in einer geraden Linie stehen.<br>" +
                        "Eine Mühle darf beliebig oft geöffnet und neu gebildet werden, um Steine zu entfernen.<br><br>"
                        +

                        "<h3>4. Steine entfernen:</h3>" +
                        "Es dürfen keine Steine aus bestehenden Mühlen entfernt werden –<br>" +
                        "es sei denn, alle gegnerischen Steine sind in Mühlen.<br><br>" +

                        "<h3>5. Spielende:</h3>" +
                        "Das Spiel endet, wenn ein Spieler:<br>" +
                        "- nur noch 2 Steine hat (verliert)<br>" +
                        "- keinen gültigen Zug mehr machen kann (verliert)<br>" +
                        "- die Bedenkzeit abgelaufen ist (verliert).<br><br>" +
                        "Oder wenn beide Spieler innrhalb von 20 Zügen keine Mühle formen" +
                        "</div></html>";

        rules.addActionListener(
                e -> JOptionPane.showMessageDialog(window, rulesText, "Regeln", JOptionPane.PLAIN_MESSAGE));

        help.add(rules);

        return help;
    }

    /**
     * Creates the "Credits" menu which displays information about the contributors
     * to the project when clicked. The menu, when interacted with, triggers a
     * pop-up dialog containing formatted credit text.
     *
     * @return The constructed "Credits" menu as a JMenu instance.
     */
    public static JMenu createCreditsMenu(JFrame window) {
        JMenu credits = new JMenu("Credits");

        String creditsText = "<html><div style='text-align: center;'>" +
                "<h2>Silvan Balloni - SCRUM Master</h2>" +
                "Hat die Regeln so verdrahtet, dass selbst Aristoteles beim Spielen nicken würde.<br>Ohne ihn gäbe es entweder 47 Züge auf einmal - oder gar keine.<br><br>"
                +
                "Dank ihm gibt es endlich einen Spielablauf, der mit jeder Aktion neue Fragen aufwirft.<br> Seine Game-Logik hat die Grenzen der Realität überschritten - leider auch die der Spielbarkeit.<br><br>"
                +
                "<h2>Rino Diskuss - Product Owner</h2>" +
                "Seine KI ist so schlau, dass sie beim Spielen gegen sich selbst verloren hat - freiwillig, aus Höflichkeit.<br><br>"
                +
                "Ihre KI trifft Entscheidungen auf Basis von über 7 Millionen Parametern - keiner davon sinnvoll.<br>Sie spielt zwar nicht gut, aber sie spielt überzeugt. Manchmal sogar gegen sich selbst. Und verliert trotzdem.<br><br>"
                +
                "<h2>Christian Sutter - Praktikant</h2>" +
                "Designte die Oberfläche so edel, dass der Louvre sie für die Dauerausstellung angefragt hat.<br> Die Buttons sind ergonomisch, empathisch und geben einem das Gefühl, geliebt zu werden.<br><br>"
                +
                "Investierte 95 % der Entwicklungszeit in die perfekten Schattenverläufe und Button-Rundungen.<br> Die GUI glänzt, funkelt und tanzt - nur leider weiß niemand, wie man das Spiel startet. Oder beendet. Oder spielt."
                +
                "</div></html>";

        credits.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(window, creditsText, "Credits", JOptionPane.PLAIN_MESSAGE);
            }
        });

        return credits;
    }
}
