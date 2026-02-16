import { View, Text, ScrollView, StyleSheet } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import { Pressable } from "react-native";
import { useRouter } from "expo-router";

export default function Statistiques() {
    const router = useRouter();

    // Exemple de données, tu pourras les remplacer par des données réelles
    const stats = {
        aideMois: 12,
        aideAnnee: 120,
        personnesSuivies: 45,
        aideRefusee: 3,
        aideAcceptee: 117,
    };

    // Composant Card pour chaque statistique
    const StatCard = ({ label, value, icon }: { label: string; value: number; icon?: string }) => (
        <View style={styles.card}>
            {/* Petite carte statistique réutilisable (icone optionnelle) */}
            {icon && <Ionicons name={icon as any} size={24} color="#1FB28A" style={{ marginBottom: 8 }} />}
            <Text style={styles.cardValue}>{value}</Text>
            <Text style={styles.cardLabel}>{label}</Text>
        </View>
    );

    return (
        <SafeAreaView style={{ flex: 1 }}>
            <ScrollView contentContainerStyle={styles.container}>
                <View style={styles.header}>
                    <Pressable onPress={() => router.back()} hitSlop={12}>
                        <Ionicons name="arrow-back" size={26} color="#111" />
                    </Pressable>
                    <Text style={styles.headerTitle}>Statistiques</Text>
                </View>

                <View style={styles.grid}>
                    <StatCard label="Personnes aidées ce mois" value={stats.aideMois} icon="calendar" />
                    <StatCard label="Personnes aidées cette année" value={stats.aideAnnee} icon="calendar-outline" />
                    <StatCard label="Personnes suivies" value={stats.personnesSuivies} icon="people" />
                    <StatCard label="Aides refusées" value={stats.aideRefusee} icon="close-circle" />
                    <StatCard label="Aides acceptées" value={stats.aideAcceptee} icon="checkmark-circle" />
                </View>
            </ScrollView>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    container: {
        padding: 16,
        paddingBottom: 40,
    },
    header: {
        height: 56,
        justifyContent: "center",
        marginBottom: 16,
    },
    headerTitle: {
        position: "absolute",
        alignSelf: "center",
        fontSize: 20,
        fontWeight: "700",
    },
    grid: {
        flexDirection: "row",
        flexWrap: "wrap",
        justifyContent: "space-between",
    },
    card: {
        width: "48%", // deux cartes par ligne
        backgroundColor: "#f5f5f5",
        borderRadius: 12,
        padding: 16,
        marginBottom: 16,
        alignItems: "center",
        shadowColor: "#000",
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.1,
        shadowRadius: 4,
        elevation: 3,
    },
    cardValue: {
        fontSize: 22,
        fontWeight: "700",
        marginBottom: 4,
        color: "#111",
    },
    cardLabel: {
        fontSize: 14,
        color: "#555",
        textAlign: "center",
    },
});
