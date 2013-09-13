import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;


/**
 * Can a thousand monkeys typing for a thousand years write a little bit of
 * Shakespeare's plays?  Let's find out.
 * 
 * Note: this class is NOT meant as an example of good coding style.
 * It also has a few bugs lurking in it, because we're using it as an example
 * of debugging.
 */
public class MonkeyShakespeare {

    public static void main(String[] args) throws Exception {
        
        // Read in all of Shakespeare from a file
        List<String> allLines = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader("shakespeare.json"));
        String line = reader.readLine();
        while (line != null) {
            // Extract the play text from lines of the form:
            //    "text_entry": "A bird of my tongue is better than a beast of yours.",
            if (line.contains("text_entry")) {
                line = line.substring(19, line.length()-2); // keep only the part between double-quotes
                allLines.add(line);
            }
        }
        reader.close();
        
        // Build an index that maps each word to the set of lines that contain the word
        Map<String, Set<String>> index = new HashMap<String, Set<String>>();
        for (String line2 : allLines) {
            StringTokenizer tokenizer = new StringTokenizer(
                    line2, // string to split up into words ("tokens")
                    " ,.!?;:()[]{}'\"-_+=<>/\\`~$|!@#$%^&*", // space & punctuation separates words
                    false  // don't keep the spaces and punctuation
            );
            while (tokenizer.hasMoreElements()) {
                String word = tokenizer.nextToken();
                word = word.toLowerCase();                
                Set<String> linesContainingWord = index.get(word);
                if (linesContainingWord == null) {
                    // First time we've seen this word -- create a set for it
                    linesContainingWord = new HashSet<String>();
                    index.put(word,  linesContainingWord);
                } else {
                    linesContainingWord.add(line);
                }
            }            
        }
        
        // count the frequency of each letter in the words actually used, so that the 
        // monkeys have a fair shot at typing ETAOIN more often than VKXJQZ.
        // letterDistribution.get(c) counts how often a character c appears in the index words
        Map<Character, Integer> letterDistribution = new HashMap<Character, Integer>();
        int sumOfLetterDistribution = 0; // sum of all counts in letterDistribution
        for (String word : index.keySet()) {
            for (int i = 0; i < word.length(); ++i) {
                letterDistribution.put(word.charAt(i), 
                                       letterDistribution.get(word.charAt(i)) + 1);
                ++sumOfLetterDistribution;
            }
        }

        // Count the frequency of each word length, too, so that the 
        // monkeys are typing THE more often than HONORIFICABILITUDINITATIBUS.
        // lengthDistribution.get(len) counts the number of words of length len in the index.
        Map<Integer, Integer> lengthDistribution = new HashMap<Integer, Integer>();
        int sumOfLengthDistribution = 0; // sum of all counts in letterDistribution
        for (String word : index.keySet()) {
            lengthDistribution.put(word.length(), 
                                   lengthDistribution.get(word.length()) + 1);
            ++sumOfLengthDistribution;
        }

        // Set those monkeys going!
        // Type one word at a time, and if we ever generate a word found 
        // somewhere in Shakespeare, print it.
        Random random = new Random(); // our random monkey
        while (true) {
            // Monkey first decides how long the word will be, 
            // randomly distributed according to lengthDistribution
            int monkeyWordLength = 1;
            int rand = random.nextInt(sumOfLengthDistribution);
            while (rand >= lengthDistribution.get(monkeyWordLength)) {
                rand -= lengthDistribution.get(monkeyWordLength);
                ++monkeyWordLength;
            }

            // Now monkey picks each letter in the word, 
            // randomly distributed according to letterDistribution
            String monkeyWord = "";
            for (int i = 0; i < monkeyWordLength; ++i) {
                char c = 'a';
                int rand2 = random.nextInt(sumOfLetterDistribution);
                while (rand2 >= letterDistribution.get(c)) {
                    rand2 -= letterDistribution.get(c);
                    ++c;
                }
                monkeyWord += c;
            }

            // Is the monkey's word in Shakespeare?
            Set<String> matchingLines = index.get(monkeyWord);
            if (matchingLines != null) {
                String randomMatchingLine = new ArrayList<String>(matchingLines)
                        .get(random.nextInt(matchingLines.size()));
                System.out.println(monkeyWord + ": " + randomMatchingLine);
            }
        }        
    }
}
