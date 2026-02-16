import { View, Text, StyleSheet, Pressable } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter } from "expo-router";

export default function AppelAidant() {
    const router = useRouter();
    const NomAidant = "Marie Dupont";

    return (
        <SafeAreaView style={{ flex: 1, backgroundColor: "#000" }}>
            <View style={styles.container}>
                {/* Écran d'appel vers l'aidant (mock) */}
                <Text style={styles.callingText}>Appel en cours</Text>
                <Text style={styles.number}>{NomAidant}</Text>
                <Text style={styles.status}>Sonnerie...</Text>

                <Pressable style={styles.endButton} onPress={() => router.back()}>
                    <Text style={styles.endButtonText}>Raccrocher</Text>
                </Pressable>
            </View>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: "center",
        alignItems: "center",
    },
    callingText: {
        color: "#fff",
        fontSize: 24,
        marginBottom: 20,
    },
    number: {
        color: "#fff",
        fontSize: 40,
        fontWeight: "bold",
        marginBottom: 10,
    },
    status: {
        color: "#aaa",
        fontSize: 18,
        marginBottom: 50,
    },
    endButton: {
        backgroundColor: "#d32f2f",
        paddingVertical: 20,
        paddingHorizontal: 60,
        borderRadius: 50,
    },
    endButtonText: {
        color: "#fff",
        fontSize: 22,
        fontWeight: "700",
    },
});
