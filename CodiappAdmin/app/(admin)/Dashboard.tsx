import AdminAlertItem from "@/components/AdminAlertItem";
import AdminStatCard from "@/components/AdminStatCard";
import { useRouter } from "expo-router";
import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

export default function AdminDashboard() {
    const router = useRouter();

    return (
        <SafeAreaView style={{ flex: 1 }}>
            <ScrollView contentContainerStyle={styles.container}>
                <Text style={styles.title}>Dashboard Admin</Text>

                {/* Stats */}
                <View style={styles.statsRow}>
                    <AdminStatCard icon="people" label="Aidés actifs" value="128" />
                    <AdminStatCard icon="medkit" label="Aidants actifs" value="42" />
                </View>

                <View style={styles.statsRow}>
                    <AdminStatCard icon="alert" label="Aides ce mois" value="96" />
                    <AdminStatCard icon="warning" label="Sans tuteur" value="7" />
                </View>

                {/* Alertes */}
                <Text style={styles.section}>Alertes</Text>
                <AdminAlertItem text="7 aidés sans tuteur assigné" />
                <AdminAlertItem text="3 aidants inactifs depuis 30 jours" />

                {/* Navigation */}
                <Text style={styles.section}>Gestion</Text>

                <Pressable
                    style={styles.link}
                    onPress={() => router.push("/(admin)/users")}

                >
                    <Text>👵 Gérer les aidés</Text>
                </Pressable>

                <Pressable
                    style={styles.link}
                    onPress={() => router.push("/(admin)/helpers")}

                >
                    <Text>🧑‍⚕️ Gérer les aidants</Text>
                </Pressable>
            </ScrollView>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    container: {
        padding: 16,
    },
    title: {
        fontSize: 24,
        fontWeight: "800",
        marginBottom: 16,
    },
    statsRow: {
        flexDirection: "row",
        gap: 12,
        marginBottom: 12,
    },
    section: {
        fontSize: 16,
        fontWeight: "700",
        marginTop: 24,
        marginBottom: 8,
    },
    link: {
        backgroundColor: "#fff",
        padding: 14,
        borderRadius: 12,
        marginBottom: 8,
    },
});
