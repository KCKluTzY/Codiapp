import { StyleSheet, Text, View, Image, Pressable, Alert } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Colors } from "../constants/Colors";
import { useRouter } from "expo-router";
import Ionicons from "@expo/vector-icons/build/Ionicons";
import * as SecureStore from "expo-secure-store";
import { useState, useEffect } from "react";

export default function Parametres() {
    const router = useRouter();
    const [user, setUser] = useState<any>(null);

    // 🔹 Charger l'utilisateur si connecté
    useEffect(() => {
        const loadUser = async () => {
            const storedUser = await SecureStore.getItemAsync("user");
            if (storedUser) setUser(JSON.parse(storedUser));
        };
        loadUser();
    }, []);

    // 🔹 Gestion de l'action Authentification / Accueil
    const handleAuthPress = () => {
        if (user) {
            if (user.role === "PERSON_DI") router.push("/home/HomeScreenUser");
            else router.push("/home/HomeScreenUser");
        } else {
            router.push("./AuthentificationUser");
        }
    };

    // 🔹 Déconnexion
    const handleLogout = async () => {
        await SecureStore.deleteItemAsync("user");
        Alert.alert("Déconnexion", "Vous avez été déconnecté.");
        setUser(null);
    };

    return (
        <SafeAreaView style={styles.safe}>
            <View style={styles.header}>
                <Pressable onPress={() => router.back()} hitSlop={12}>
                    <Ionicons name="arrow-back" size={28} color="white" />
                </Pressable>
                <Text style={styles.title}>Paramètres</Text>
            </View>

            <View style={styles.content}>
                <Pressable onPress={handleAuthPress}>
                    <View style={styles.card}>
                        <Image
                            source={require("../assets/images/authentification.png")}
                            style={styles.logo}
                        />
                        <View style={styles.textContainer}>
                            <Text style={styles.cardTitle}>Authentification</Text>
                            <Text style={styles.cardSubTitle}>
                                {user ? "Accéder à l'accueil" : "Se connecter"}
                            </Text>
                        </View>
                    </View>
                </Pressable>

                {user && (
                    <Pressable onPress={handleLogout}>
                        <View style={[styles.card, { marginTop: 16 }]}>
                            <Image
                                source={require("../assets/images/logout.png")}
                                style={styles.logo}
                            />
                            <View style={styles.textContainer}>
                                <Text style={styles.cardTitle}>Déconnexion</Text>
                                <Text style={styles.cardSubTitle}>
                                    Se déconnecter de l'application
                                </Text>
                            </View>
                        </View>
                    </Pressable>
                )}
            </View>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    safe: { flex: 1, backgroundColor: Colors.background },
    header: { paddingHorizontal: 16, paddingTop: 8, paddingBottom: 12, backgroundColor: Colors.primary },
    title: { marginTop: 18, fontSize: 26, fontWeight: "800", textAlign: "center", color: "#fff" },
    content: { flex: 1, padding: 24 },
    card: { backgroundColor: Colors.primary, borderRadius: 24, padding: 15, borderColor: Colors.primary_light, borderWidth: 5, flexDirection: "row", alignItems: "center" },
    logo: { width: 60, height: 60, marginRight: 12 },
    textContainer: { flex: 1 },
    cardTitle: { fontSize: 18, fontWeight: "600", color: "#000" },
    cardSubTitle: { fontSize: 14, color: "#333", marginTop: 2 },
});