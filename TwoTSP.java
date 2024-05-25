import java.io.*;
import java.util.*;

public class TwoTSP {
    static class City {
        int id;
        int x, y;

        City(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        // project description:
        // Distance between two cities is defined as the Euclidian distance rounded to the nearest integer.
        // In other words, you will compute distance between two cities 𝑐1=(𝑥1,𝑦1) and 𝑐2=(𝑥2,𝑦2) as follows:
        double distanceTo(City other) {
            return Math.round(Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2)));
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to the solution of the project!");
        System.out.println("To select input files from the Inputs folder, please enter the file name with the extension.");
        System.out.println("For example, to select 'example-input-1.txt', enter 'Inputs/example-input-1.txt'\n");
        // scanner for reading inputs
        Scanner scanner = new Scanner(System.in);

        // "Inputs/example-input-1.txt"
        System.out.print("Enter the input file path: ");
        String inputFileName = scanner.nextLine();

        // "project-output-3.txt"
        System.out.print("Enter the output file path: ");
        String outputFileName = scanner.nextLine();

        List<City> cities = readCitiesFromFile(inputFileName);

        // int numCities = cities.size();

        // splitting cities into two groups
        List<City> group1 = new ArrayList<>();
        List<City> group2 = new ArrayList<>();
        splitCities(cities, group1, group2);

        // create tours for each salesmen
        List<City> tour1 = nearestNeighborTour(group1);
        List<City> tour2 = nearestNeighborTour(group2);

        // optimizing
        tour1 = twoOptOptimization(tour1);
        tour2 = twoOptOptimization(tour2);

        // calculating total distances
        double tour1Distance = calculateTotalDistanceInTour(tour1);
        double tour2Distance = calculateTotalDistanceInTour(tour2);
        double totalDistance = tour1Distance + tour2Distance;

        // writing the output to the file
        writeOutputToFile(outputFileName, totalDistance, tour1, tour2);
    }

    private static List<City> readCitiesFromFile(String fileName) throws IOException {
        List<City> cities = new ArrayList<>();
        BufferedReader fileReader = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = fileReader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;  // Skip empty lines
            String[] parts = line.trim().split("\\s+");
            int id = Integer.parseInt(parts[0]);
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            cities.add(new City(id, x, y));
        }
        fileReader.close();
        return cities;
    }

    private static void splitCities(List<City> cityList, List<City> group1, List<City> group2) {
        Collections.shuffle(cityList);
        for (int i = 0; i < cityList.size(); i++) {
            if (i % 2 == 0) {
                group1.add(cityList.get(i));
            } else {
                group2.add(cityList.get(i));
            }
        }
    }

    private static List<City> nearestNeighborTour(List<City> cities) {
        List<City> tour = new ArrayList<>();
        Set<City> visited = new HashSet<>();
        City current = cities.getFirst();
        tour.add(current);
        visited.add(current);

        while (tour.size() < cities.size()) {
            City next = cities.stream()
                    .filter(city -> !visited.contains(city))
                    .min(Comparator.comparingDouble(current::distanceTo))
                    .orElse(null);

            if (next == null) break;
            tour.add(next);
            visited.add(next);
            current = next;
        }

        return tour;
    }

    private static List<City> twoOptOptimization(List<City> tour) {
        int tourSize = tour.size();
        boolean ifOptimized = true;
        while (ifOptimized) {
            ifOptimized = false;
            for (int i = 0; i < tourSize - 1; i++) {
                for (int j = i + 2; j < tourSize; j++) {
                    if (j - i == 1) continue;
                    double delta = distanceAtoB(tour.get(i), tour.get(j))
                            + distanceAtoB(tour.get(i + 1), tour.get((j + 1) % tourSize))
                            - distanceAtoB(tour.get(i), tour.get(i + 1))
                            - distanceAtoB(tour.get(j), tour.get((j + 1) % tourSize));
                    if (delta < 0) {
                        reverse(tour, i + 1, j);
                        ifOptimized = true;
                    }
                }
            }
        }
        return tour;
    }

    private static double distanceAtoB(City a, City b) {
        return a.distanceTo(b);
    }

    private static void reverse(List<City> tour, int start, int end) {
        while (start < end) {
            City temp = tour.get(start);
            tour.set(start, tour.get(end));
            tour.set(end, temp);
            start++;
            end--;
        }
    }

    private static double calculateTotalDistanceInTour(List<City> tour) {
        double totalDistance = 0;
        for (int i = 0; i < tour.size() - 1; i++) {
            totalDistance += tour.get(i).distanceTo(tour.get(i + 1));
        }
        totalDistance += tour.getLast().distanceTo(tour.getFirst());
        return totalDistance;
    }

    private static void writeOutputToFile(String fileName, double totalDistance, List<City> tour1, List<City> tour2) throws IOException {
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileName));
        fileWriter.write((int) totalDistance + "\n");
        writeTourToFile(fileWriter, tour1);
        fileWriter.write("\n");
        writeTourToFile(fileWriter, tour2);
        fileWriter.close();
    }

    private static void writeTourToFile(BufferedWriter fileWriter2, List<City> tour) throws IOException {
        fileWriter2.write((int) calculateTotalDistanceInTour(tour) + " " + tour.size() + "\n");
        for (City city : tour) {
            fileWriter2.write(city.id + "\n");
        }
    }
}
