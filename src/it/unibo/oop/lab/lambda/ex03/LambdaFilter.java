package it.unibo.oop.lab.lambda.ex03;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Modify this small program adding new filters.
 * Realize this exercise using as much as possible the Stream library.
 * 
 * 1) Convert to lower-case
 * 
 * 2) Count the number of chars
 * 
 * 3) Count the number of lines
 * 
 * 4) List all the words in alphabetical order
 * 
 * 5) Write the count for each word, e.g. "word word pippo" should output "pippo -> 1 word -> 2"
 *
 */
public final class LambdaFilter extends JFrame {

    private static final long serialVersionUID = 1760990730218643730L;

    private enum Command {
        IDENTITY("No modifications", Function.identity()),
        LOWERCASE("Convert to lowercase", i -> i.toLowerCase()),
        NUM_OF_CHARS("Number of characters", i -> String.valueOf(i.chars().count())),
        NUM_OF_LINES("Number of lines", i -> String.valueOf(i.lines().count())),
        ALPHABETICAL_ORDER("List words in alphabetical order", i -> Pattern.compile("[^\\w]+")
                .splitAsStream(i)
                .sorted((x, y) -> x.compareTo(y))
                .reduce((x, y) -> x + "\n" + y)
                .get()
    	),
    	WORD_COUNT("Count the occurences", i -> {
    		Map<String, Integer> countWords = new HashMap<>();
    		Pattern.compile("[^\\w]+").splitAsStream(i)
    			.forEach(j -> {
    				if (countWords.containsKey(j)) {
    					countWords.put(j, countWords.get(j) + 1);
    				} else {
    					countWords.put(j, 1);
    				}
    			});
    		System.out.println(countWords);
    		String s = "";
    		for (var entry : countWords.entrySet()) {
    			s = s + entry.getKey() + " -> " + entry.getValue() + "\n";
    		}
    		return s;
    	});

        private final String commandName;
        private final Function<String, String> fun;

        Command(final String name, final Function<String, String> process) {
            commandName = name;
            fun = process;
        }

        @Override
        public String toString() {
            return commandName;
        }

        public String translate(final String s) {
            return fun.apply(s);
        }
    }

    private LambdaFilter() {
        super("Lambda filter GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JPanel panel1 = new JPanel();
        final LayoutManager layout = new BorderLayout();
        panel1.setLayout(layout);
        /*
         * values() is a static method added by the compiler (hence no javadoc), 
         * that returns an array containing all possible values for the enumeration
         */
        final JComboBox<Command> combo = new JComboBox<>(Command.values()); 
        panel1.add(combo, BorderLayout.NORTH);
        final JPanel centralPanel = new JPanel(new GridLayout(1, 2));
        final JTextArea left = new JTextArea();
        left.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        final JTextArea right = new JTextArea();
        right.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        right.setEditable(false);
        centralPanel.add(left);
        centralPanel.add(right);
        panel1.add(centralPanel, BorderLayout.CENTER);
        final JButton apply = new JButton("Apply");
        apply.addActionListener(ev -> right.setText(((Command) combo.getSelectedItem()).translate(left.getText())));
        panel1.add(apply, BorderLayout.SOUTH);
        this.setContentPane(panel1);
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int sw = (int) screen.getWidth();
        final int sh = (int) screen.getHeight();
        setSize(sw / 4, sh / 4);
        setLocationByPlatform(true);
    }

    /**
     * @param a unused
     */
    public static void main(final String... a) {
        final LambdaFilter gui = new LambdaFilter();
        gui.setVisible(true);
    }
}
