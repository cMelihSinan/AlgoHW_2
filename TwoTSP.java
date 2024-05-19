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

        double distanceTo(City other) {
            return Math.round(Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2)));
        }
    }

    public static void main(String[] args) throws IOException {
        // Read input
        String inputFileName = "Inputs/example-input-3.txt";
        String outputFileName = "sinan-output-3.txt";
        List<City> cities = readCitiesFromFile(inputFileName);
        int numCities = cities.size();

        // Split cities into two groups for each salesman
        List<City> group1 = new ArrayList<>();
        List<City> group2 = new ArrayList<>();
        splitCities(cities, group1, group2);

        // Create tours for both salesmen
        List<City> tour1 = nearestNeighborTour(group1);
        List<City> tour2 = nearestNeighborTour(group2);

        // Optimize tours
        tour1 = twoOptOptimization(tour1);
        tour2 = twoOptOptimization(tour2);

        // Calculate total distances
        double tour1Distance = calculateTotalDistance(tour1);
        double tour2Distance = calculateTotalDistance(tour2);
        double totalDistance = tour1Distance + tour2Distance;

        // Write output
        writeOutputToFile(outputFileName, totalDistance, tour1, tour2);
    }

    private static List<City> readCitiesFromFile(String fileName) throws IOException {
        List<City> cities = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue;  // Skip empty lines
            String[] parts = line.trim().split("\\s+");
            int id = Integer.parseInt(parts[0]);
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            cities.add(new City(id, x, y));
        }
        br.close();
        return cities;
    }

    private static void splitCities(List<City> cities, List<City> group1, List<City> group2) {
        Collections.shuffle(cities);
        for (int i = 0; i < cities.size(); i++) {
            if (i % 2 == 0) {
                group1.add(cities.get(i));
            } else {
                group2.add(cities.get(i));
            }
        }
    }

    private static List<City> nearestNeighborTour(List<City> cities) {
        List<City> tour = new ArrayList<>();
        Set<City> visited = new HashSet<>();
        City current = cities.get(0);
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
        int size = tour.size();
        boolean improved = true;
        while (improved) {
            improved = false;
            for (int i = 0; i < size - 1; i++) {
                for (int j = i + 2; j < size; j++) {
                    if (j - i == 1) continue;
                    double delta = distance(tour.get(i), tour.get(j))
                            + distance(tour.get(i + 1), tour.get((j + 1) % size))
                            - distance(tour.get(i), tour.get(i + 1))
                            - distance(tour.get(j), tour.get((j + 1) % size));
                    if (delta < 0) {
                        reverse(tour, i + 1, j);
                        improved = true;
                    }
                }
            }
        }
        return tour;
    }

    private static double distance(City a, City b) {
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

    private static double calculateTotalDistance(List<City> tour) {
        double totalDistance = 0;
        for (int i = 0; i < tour.size() - 1; i++) {
            totalDistance += tour.get(i).distanceTo(tour.get(i + 1));
        }
        totalDistance += tour.get(tour.size() - 1).distanceTo(tour.get(0));
        return totalDistance;
    }

    private static void writeOutputToFile(String fileName, double totalDistance, List<City> tour1, List<City> tour2) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        bw.write((int) totalDistance + "\n");
        writeTourToFile(bw, tour1);
        bw.write("\n");
        writeTourToFile(bw, tour2);
        bw.close();
    }

    private static void writeTourToFile(BufferedWriter bw, List<City> tour) throws IOException {
        bw.write((int) calculateTotalDistance(tour) + " " + tour.size() + "\n");
        for (City city : tour) {
            bw.write(city.id + "\n");
        }
    }
}
