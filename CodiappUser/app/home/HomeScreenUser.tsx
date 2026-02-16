import { Image, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import DailyRoad from "../../components/DailyRoad";
import EmergencyButton from "../../components/EmergencyButton";
import StatusCard from "../../components/StatusCard";
import { Colors } from "../../constants/Colors";
import { useRouter } from "expo-router";

export default function HomeScreenUser() {
    const router = useRouter();

    return (
        <SafeAreaView style={{ flex: 1 }}>
            <ScrollView style={styles.container}>
                {/* Accès rapide paramètres + logo + sections principales */}
                <Pressable onPress={() => router.push("/Parametres")}>
                    <Image
                        source={require("../../assets/images/parametre.png")}
                        style={styles.parametre}
                    />
                </Pressable>

                <View style={styles.logoContainer}>
                    <Image source={require("../../assets/images/logo.png")} style={styles.logo} />
                </View>

                <Text style={styles.title}>Codi App</Text>

                {/* Statut + bouton d'urgence + trajet du jour */}
                <StatusCard />
                <EmergencyButton />
                <DailyRoad />
            </ScrollView>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: Colors.background, padding: 16 },
    title: { fontSize: 22, fontWeight: "700", marginBottom: 16, textAlign: "center", color: "#36029d" },
    logo: { width: 100, height: 100 },
    logoContainer: { alignItems: "center", marginBottom: 0.2 },
    parametre: { width: 40, height: 40, marginLeft: 300, position: "absolute", top: 16, right: 16, zIndex: 10 },
});