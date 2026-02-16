import {
    StyleSheet,
    Text,
    View,
    Image,
    Pressable,
    TextInput,
    Alert,
    ActivityIndicator,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Colors } from "../constants/Colors";
import { useRouter } from "expo-router";
import Ionicons from "@expo/vector-icons/build/Ionicons";
import { useState, useEffect } from "react";
import * as SecureStore from "expo-secure-store";

export default function AuthentificationUser() {
    const router = useRouter();
    const [identifier, setIdentifier] = useState("");
    const [password, setPassword] = useState("");
    const [loading, setLoading] = useState(false);

    // 🔹 Redirige si déjà connecté (stockage sécurisé via SecureStore)
    useEffect(() => {
        const checkUser = async () => {
            const storedUser = await SecureStore.getItemAsync("user");
            if (storedUser) {
                router.replace("/home/HomeScreenUser");
            }
        };
        checkUser();
    }, []);

    // Normalise le username renvoyé par le backend pour affichage
    const formatUsername = (raw: string) => {
        return raw
            .replace(/_/g, " ")
            .toLowerCase()
            .replace(/\b\w/g, l => l.toUpperCase());
    };

    // Envoi des identifiants au backend et stockage sécurisé des tokens
    const handleLogin = async () => {
        if (!identifier || !password) {
            Alert.alert("Erreur", "Veuillez remplir tous les champs");
            return;
        }

        try {
            setLoading(true);

            const response = await fetch(
                "http://10.0.2.2:8080/api/v1/auth/login",
                {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ identifier, password }),
                }
            );

            const data = await response.json();
            if (!response.ok) throw new Error(data.message || "Erreur de connexion");

            const formattedUsername = formatUsername(data.username);

            const userToStore = {
                id: data.userId,
                username: formattedUsername,
                role: data.role,
                accessToken: data.accessToken,
                refreshToken: data.refreshToken,
            };

            await SecureStore.setItemAsync("user", JSON.stringify(userToStore));

            Alert.alert("Succès", "Connexion réussie ✅");
            router.replace("/home/HomeScreenUser");
        } catch (err: any) {
            Alert.alert("Erreur", err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <SafeAreaView style={styles.safe}>
            <View style={styles.header}>
                <Pressable onPress={() => router.back()} hitSlop={12}>
                    <Ionicons name="arrow-back" size={28} color="white" />
                </Pressable>
                <Text style={styles.title}>Authentification</Text>
            </View>

            <View style={styles.content}>
                <View style={styles.card}>
                    <Image
                        source={require("../assets/images/authentification.png")}
                        style={styles.logo}
                    />
                    <View style={styles.textContainer}>
                        <Text style={styles.cardTitle}>Connexion</Text>
                        <Text style={styles.cardSubTitle}>Entrez vos identifiants</Text>
                    </View>
                </View>

                <TextInput
                    placeholder="Email ou username"
                    placeholderTextColor="#666"
                    style={styles.input}
                    value={identifier}
                    onChangeText={setIdentifier}
                    autoCapitalize="none"
                />

                <TextInput
                    placeholder="Mot de passe"
                    placeholderTextColor="#666"
                    secureTextEntry
                    style={styles.input}
                    value={password}
                    onChangeText={setPassword}
                />

                <Pressable style={styles.button} onPress={handleLogin}>
                    {loading ? <ActivityIndicator color="#fff" /> : <Text style={styles.buttonText}>Se connecter</Text>}
                </Pressable>
            </View>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    safe: { flex: 1, backgroundColor: Colors.background },
    header: { paddingHorizontal: 16, paddingTop: 8, paddingBottom: 12, backgroundColor: Colors.primary },
    title: { marginTop: 18, fontSize: 26, fontWeight: "800", textAlign: "center", color: "#fff" },
    content: { flex: 1, padding: 24 },
    card: { backgroundColor: Colors.primary, borderRadius: 24, padding: 15, borderColor: Colors.primary_light, borderWidth: 5, flexDirection: "row", alignItems: "center", marginBottom: 32 },
    logo: { width: 60, height: 60, marginRight: 12 },
    textContainer: { flex: 1 },
    cardTitle: { fontSize: 18, fontWeight: "600", color: "#000" },
    cardSubTitle: { fontSize: 14, color: "#333", marginTop: 2 },
    input: { backgroundColor: "#fff", borderRadius: 16, paddingHorizontal: 16, paddingVertical: 14, fontSize: 16, marginBottom: 16, borderColor: Colors.primary_light, borderWidth: 2 },
    button: { backgroundColor: Colors.primary, borderRadius: 20, paddingVertical: 14, alignItems: "center", borderColor: Colors.primary_light, borderWidth: 3, marginTop: 8 },
    buttonText: { fontSize: 18, fontWeight: "700", color: "#fff" },
});