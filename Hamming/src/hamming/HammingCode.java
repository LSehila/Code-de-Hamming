package hamming;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HammingCode {

    /**
     * Cette méthode effectue une addition binaire entre deux bits.
     * @param bit1 Le premier bit.
     * @param bit2 Le deuxième bit.
     * @return Le résultat de l'addition binaire (0 ou 1).
     */
    private static int binaryAddition(int bit1, int bit2) {
        if (bit1 == bit2)
            return 0;
        return 1;
    }

    /**
     * Cette méthode génère toutes les combinaisons binaires possibles pour un bit invariant.
     * @param invariantPosition La position du bit invariant.
     * @param numberOfBits Le nombre total de bits.
     * @return Une liste de tableaux représentant les combinaisons binaires.
     */
    private static List<Integer[]> generateBinaryCombinations(int invariantPosition, int numberOfBits) {
        List<Integer[]> combinations = new ArrayList<>();
        int maxCombinations = (int) Math.pow(2, numberOfBits - 1); // Nombre total de combinaisons

        for (int i = 0; i < maxCombinations; i++) {
            Integer[] binaryArray = new Integer[numberOfBits];

            // Initialiser tous les bits à 0
            Arrays.fill(binaryArray, 0);

            // Fixer le bit invariant à 1
            binaryArray[invariantPosition] = 1;

            // Convertir i en binaire et le remplir dans binaryArray
            String binaryString = String.format("%" + (numberOfBits - 1) + "s", Integer.toBinaryString(i)).replace(' ', '0');

            int index = 0;
            for (int j = 0; j < numberOfBits; j++) {
                if (j != invariantPosition) {
                    binaryArray[j] = binaryString.charAt(index) - '0';
                    index++;
                }
            }

            combinations.add(binaryArray);
        }

        return combinations;
    }

    /**
     * Cette méthode convertit un tableau de bits binaires en un nombre décimal.
     * @param binaryArray Le tableau de bits binaires.
     * @return Le nombre décimal correspondant.
     */
    public static int binaryToDecimal(Integer[] binaryArray) {
        int decimalValue = 0;
        for (int i = 0; i < binaryArray.length; i++) {
            decimalValue += binaryArray[i] * Math.pow(2, binaryArray.length - 1 - i);
        }
        return decimalValue;
    }

    /**
     * Cette méthode génère toutes les combinaisons décimales possibles pour un bit invariant.
     * @param invariantPosition La position du bit invariant.
     * @param numberOfBits Le nombre total de bits.
     * @return Une liste de nombres décimaux représentant les combinaisons.
     */
    private static List<Integer> generateDecimalCombinations(int invariantPosition, int numberOfBits) {
        List<Integer[]> binaryCombinations = generateBinaryCombinations(invariantPosition, numberOfBits);
        return binaryCombinations.stream().map(HammingCode::binaryToDecimal).toList();
    }

    /**
     * Cette méthode calcule les indices nécessaires pour déterminer les bits de contrôle.
     * @param numberOfBits Le nombre total de bits.
     * @return Une liste de listes d'indices.
     */
    private static List<List<Integer>> calculateControlBitIndices(int numberOfBits) {
        List<List<Integer>> indices = new ArrayList<>();
        for (int i = 0; i < numberOfBits; i++) {
            indices.add(generateDecimalCombinations(i, numberOfBits));
        }
        return indices;
    }

    /**
     * Cette méthode calcule le nombre de bits de contrôle nécessaires pour l'émission.
     * @param messageLength La longueur du message.
     * @return Le nombre de bits de contrôle.
     */
    private static int calculateControlBitsForEmission(int messageLength) {
        int controlBits = 0;

        while (messageLength != ((int) (Math.pow(2, controlBits)) - 1 - controlBits)) {
            controlBits++;
        }

        return controlBits;
    }

    /**
     * Cette méthode calcule le nombre de bits de contrôle nécessaires pour la réception.
     * @param totalLength La longueur totale du message reçu.
     * @return Le nombre de bits de contrôle.
     */
    private static int calculateControlBitsForReception(int totalLength) {
        int controlBits = 0;

        while (totalLength != ((int) (Math.pow(2, controlBits)) - 1)) {
            controlBits++;
        }

        return controlBits;
    }

    /**
     * Cette méthode détermine les positions des bits de contrôle dans le mot de Hamming.
     * @param numberOfControlBits Le nombre de bits de contrôle.
     * @return Une liste des positions des bits de contrôle.
     */
    private static List<Integer> determineControlBitPositions(int numberOfControlBits) {
        List<Integer> positions = new ArrayList<>();

        for (int i = 0; i < numberOfControlBits; i++) {
            positions.add(i);
        }

        for (int i = 2; i < numberOfControlBits; i++) {
            positions.set(i, (int) Math.pow(2, positions.get(i)) - 1);
        }

        return positions;
    }

    /**
     * Cette méthode encode un message en utilisant le code de Hamming.
     * @param message Le message binaire à encoder.
     * @return Le message encodé avec les bits de contrôle.
     */
    public static int[] encodeHamming(String message) {
        List<Integer> originalMessage = Arrays.asList(message.split("")).stream().map(Integer::parseInt).toList();

        int controlBits = calculateControlBitsForEmission(originalMessage.size());
        int[] encodedMessage = new int[controlBits + originalMessage.size()];

        Arrays.fill(encodedMessage, 0);

        // Copier le message original dans le message à transmettre
        List<Integer> controlBitPositions = determineControlBitPositions(controlBits);
        controlBitPositions.sort(Comparator.reverseOrder());

        int globalIndex = encodedMessage.length - 1;
        int localIndex = 0;
        while (globalIndex >= 0) {
            if (!controlBitPositions.contains(globalIndex)) {
                encodedMessage[encodedMessage.length - 1 - globalIndex] = originalMessage.get(localIndex);
                localIndex++;
            }
            globalIndex--;
        }

        // Calcul des bits de contrôle pour l'émission
        List<List<Integer>> controlBitIndices = calculateControlBitIndices(controlBits);
        for (int i = 0; i < controlBits; i++) {
            int binarySum = 0;
            for (int j = 0; j < controlBitIndices.get(i).size(); j++) {
                binarySum = binaryAddition(binarySum, encodedMessage[encodedMessage.length - controlBitIndices.get(i).get(j)]);
            }
            encodedMessage[encodedMessage.length - 1 - controlBitPositions.get(i)] = (binarySum == 0) ? 0 : 1;
        }

        return encodedMessage;
    }
    
    /**
     * Cette méthode retourne les positions des bits de contrôle dans le message encodé.
     * @param length La longueur totale du message encodé.
     * @return Un tableau contenant les positions des bits de contrôle.
     */
    public static Integer[] getControlBitsPositions(int length) {
        List<Integer> positions = new ArrayList<>();

        int i = 0;
        while ((1 << i) - 1 < length) {  // 2^i - 1 < length
            positions.add((1 << i) - 1); // Ajoute la position du bit de contrôle
            i++;
        }

        return positions.toArray(new Integer[0]);
    }


    /**
     * Cette méthode décode un message reçu en utilisant le code de Hamming.
     * @param receivedMessage Le message reçu.
     * @return Les bits de contrôle calculés.
     */
    public static Integer[] decodeHamming(String receivedMessage) {
        List<Integer> receivedBits = Arrays.asList(receivedMessage.split("")).stream().map(Integer::parseInt).toList();
        int controlBits = calculateControlBitsForReception(receivedBits.size());

        List<List<Integer>> controlBitIndices = calculateControlBitIndices(controlBits);

        Integer[] controlBitsCalculated = new Integer[controlBits];

        for (int i = 0; i < controlBits; i++) {
            int sum = 0;
            for (int j = 0; j < controlBitIndices.get(i).size(); j++) {
                sum = binaryAddition(sum, receivedBits.get(receivedBits.size() - 1 - (controlBitIndices.get(i).get(j) - 1)));
            }
            controlBitsCalculated[i] = sum;
        }

        return controlBitsCalculated;
    }

    /**
     * Cette méthode corrige un message reçu en utilisant les bits de contrôle.
     * @param receivedMessage Le message reçu.
     * @return Le message corrigé.
     */
    public static List<Integer> correctHamming(String receivedMessage) {
      List<Integer> receivedBits = new ArrayList<>(
          Arrays.stream(receivedMessage.split(""))
                .map(Integer::parseInt)
                .collect(Collectors.toList()) // Génère une liste modifiable
      );

      // Calculer la position de l'erreur
      int errorPosition = binaryToDecimal(decodeHamming(receivedMessage));

      // Afficher la position de l'erreur en décimal
      System.out.println("Position de l'erreur (en décimal) : " + errorPosition);

      // Si une erreur est détectée, corriger le bit à la position correspondante
      if (errorPosition != 0) {
          receivedBits.set(receivedBits.size() - errorPosition, 
                           receivedBits.get(receivedBits.size() - errorPosition) == 0 ? 1 : 0);
      }

      return receivedBits;
  }

    
    /**
     * Vérifie si la longueur du message (avant encodage) respecte la règle de Hamming :
     * y = (2^i - 1) - i
     *
     * @param message Le message binaire saisi par l'utilisateur avant encodage.
     * @return true si la longueur du message est valide, false sinon.
     */
    public static boolean isMessageValid(String message) {
        int length = message.length();
        int i = 1;

        while ((Math.pow(2, i) - 1 - i) <= length) {
            if ((Math.pow(2, i) - 1 - i) == length) {
                return true; // Longueur du message correcte pour l'encodage
            }
            i++;
        }

        return false; // Longueur du message non valide
    }
    
    /**
     * Vérifie si la longueur du mot de Hamming (trame complète après encodage) respecte la règle :
     * x = 2^i - 1
     *
     * @param hammingCode La trame binaire reçue.
     * @return true si la longueur de la trame est valide, false sinon.
     */
    public static boolean isHammingCodeValid(String hammingCode) {
        int length = hammingCode.length();
        int i = 1;

        while ((Math.pow(2, i) - 1) <= length) {
            if ((Math.pow(2, i) - 1) == length) {
                return true; // La trame est valide pour la vérification
            }
            i++;
        }

        return false; // Longueur de la trame non valide
    }




    /**
     * Cette méthode affiche un tableau d'entiers avec un format amélioré.
     * @param array Le tableau à afficher.
     */
    public static void printArray(int[] array) {
        System.out.print("[ ");
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + " ");
        }
        System.out.println("]");
    }
    
    /**
     * Cette méthode affiche une liste d'entiers avec un format amélioré.
     * @param list La liste à afficher.
     */
    public static void printList(List<Integer> list) {
        System.out.print("[ ");
        for (int i = 0; i < list.size(); i++) {
            System.out.print(list.get(i) + " ");
        }
        System.out.println("]");
    }

    public static void main(String[] args) {
      System.out.println("=== Calcul des indices de bits de contrôle ===");
      calculateControlBitIndices(4).stream()
              .map(list -> list.stream())
              .forEach(stream -> {
                  stream.forEach(number -> System.out.print(number + " "));
                  System.out.println();
              });

      System.out.println("\n=== Encodage du message binaire ===");
      int[] encodedMessage = encodeHamming("1011");
      System.out.println("Message encodé : ");
      printArray(encodedMessage);

      System.out.println("\n=== Décodage du message binaire ===");
      int[] decodedMessage = Arrays.stream(decodeHamming("1101101"))
                                   .mapToInt(Integer::intValue)
                                   .toArray();
      System.out.println("Bits de contrôle calculés : ");
      printArray(decodedMessage);
      

      System.out.println("\n=== Correction du message binaire ===");
      List<Integer> correctedMessage = correctHamming("1101101");
      System.out.println("Message corrigé : ");
      printList(correctedMessage);

      System.out.println("\n=== Vérification de la validité du message ===");
      String testMessage = "1011";
      if (isMessageValid(testMessage)) {
          System.out.println("Le message '" + testMessage + "' est valide.");
      } else {
          System.out.println("Le message '" + testMessage + "' n'est pas valide.");
      }
  }
}