import java.io.*;
import java.util.*;

public class Main {

    static class City {
        int id, x, y;

        City(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }

    static int distance(City c1, City c2) {
        return (int) Math.round(Math.sqrt(Math.pow(c1.x - c2.x, 2) + Math.pow(c1.y - c2.y, 2)));
    }

    static int calculateTourDistance(List<City> tour) {
        int distance = 0;
        for (int i = 0; i < tour.size() - 1; i++) {
            distance += distance(tour.get(i), tour.get(i + 1));
        }
        distance += distance(tour.get(tour.size() - 1), tour.get(0));
        return distance;
    }

    static List<City> readCities(String filename) {
        List<City> cities = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                String[] parts = line.split(" ");
                if (parts.length != 3) {
                    System.out.println("Warning: Skipping improperly formatted line: " + line);
                    continue; // Skip improperly formatted lines
                }
                try {
                    int id = Integer.parseInt(parts[0]);
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    cities.add(new City(id, x, y));
                } catch (NumberFormatException e) {
                    System.out.println("Warning: Skipping line with invalid numbers: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: Input file not found. Please check the file path: " + filename);
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            System.out.println("Error reading input file: " + filename);
            e.printStackTrace();
            return null;
        }
        return cities;
    }

    static void writeOutput(String filename, int totalDistance, List<City> tour1, List<City> tour2) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println(totalDistance);
            pw.println(calculateTourDistance(tour1) + " " + tour1.size());
            for (City city : tour1) {
                pw.println(city.id);
            }
            pw.println();
            pw.println(calculateTourDistance(tour2) + " " + tour2.size());
            for (City city : tour2) {
                pw.println(city.id);
            }
            pw.println();
        } catch (IOException e) {
            System.out.println("Error writing output file.");
            e.printStackTrace();
        }
    }

    static List<City> twoOptSwap(List<City> tour, int i, int k) {
        List<City> newTour = new ArrayList<>(tour.subList(0, i));
        List<City> reversedSegment = tour.subList(i, k + 1);
        Collections.reverse(reversedSegment);
        newTour.addAll(reversedSegment);
        newTour.addAll(tour.subList(k + 1, tour.size()));
        return newTour;
    }

    static List<City> twoOpt(List<City> tour) {
        int bestDistance = calculateTourDistance(tour);
        boolean improvement = true;

        while (improvement) {
            improvement = false;
            for (int i = 1; i < tour.size() - 1; i++) {
                for (int k = i + 1; k < tour.size(); k++) {
                    List<City> newTour = twoOptSwap(tour, i, k);
                    int newDistance = calculateTourDistance(newTour);
                    if (newDistance < bestDistance) {
                        tour = newTour;
                        bestDistance = newDistance;
                        improvement = true;
                    }
                }
            }
        }
        return tour;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the input file path: ");
        String inputFile = scanner.nextLine();

        System.out.print("Enter the output file path: ");
        String outputFile = scanner.nextLine();

        List<City> cities = readCities(inputFile);
        if (cities == null || cities.isEmpty()) {
            System.out.println("Error reading input file. Exiting...");
            return;
        }

        // Initial tours (for simplicity, we'll split the cities into two equal parts)
        List<City> tour1 = new ArrayList<>();
        List<City> tour2 = new ArrayList<>();
        for (int i = 0; i < cities.size(); i++) {
            if (i % 2 == 0) {
                tour1.add(cities.get(i));
            } else {
                tour2.add(cities.get(i));
            }
        }

        // Improve the tours using 2-opt
        tour1 = twoOpt(tour1);
        tour2 = twoOpt(tour2);

        // Calculate total distances
        int distance1 = calculateTourDistance(tour1);
        int distance2 = calculateTourDistance(tour2);
        int totalDistance = distance1 + distance2;

        // Write output to file
        writeOutput(outputFile, totalDistance, tour1, tour2);

        System.out.println("Output written to " + outputFile);
    }
}
