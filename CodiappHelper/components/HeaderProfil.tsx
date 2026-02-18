import { View, Text, StyleSheet, Pressable, Alert } from "react-native";
import { MaterialIcons } from "@expo/vector-icons";
import StatsRow from "./StatsRow";
import { useRouter, useFocusEffect } from "expo-router";
import { useState, useCallback } from "react";
import * as SecureStore from "expo-secure-store";

type AuthUser = {
    id: string;
    username: string;
    role: string;
    accessToken: string;
    refreshToken: string;
};

export default function HeaderProfile() {
    const router = useRouter();
    const [user, setUser] = useState<AuthUser | null>(null);

    // 🔹 Formatage username backend
    const formatUsername = (raw: string) => {
        return raw
            .replace(/_/g, " ")
            .toLowerCase()
            .replace(/\b\w/g, l => l.toUpperCase());
    };

    //  Charger user à chaque focus
    useFocusEffect(
        useCallback(() => {
            const loadUser = async () => {
                const storedUserRaw =
                    await SecureStore.getItemAsync("user");

                if (storedUserRaw) {
                    const storedUser = JSON.parse(storedUserRaw);

                    storedUser.username =
                        formatUsername(storedUser.username);

                    setUser(storedUser);
                }
            };

            loadUser();
        }, [])
    );

    //  Logout
    const handleLogout = async () => {
        await SecureStore.deleteItemAsync("user");
        Alert.alert("Déconnexion", "Vous avez été déconnecté.");
        router.replace("/Authentification");
    };

    const initial = user?.username?.charAt(0).toUpperCase() ?? "?";
    const roleLabel =
        user?.role === "HELPER" ? "Aidant(e)" : "Aidant(e)"; // Affichage plus lisible du rôle

    return (
        <View style={styles.container}>
            <View style={styles.left}>
                <View style={styles.avatar}>
                    <Text style={styles.avatarText}>
                        {initial}
                    </Text>
                </View>

                <View>
                    <Text style={styles.name}>
                        {user?.username ?? "Utilisateur"}
                    </Text>
                    <Text style={styles.role}>
                        {roleLabel}
                    </Text>
                </View>
            </View>

            {/* Boutons action : paramètres et déconnexion */}
            <View style={styles.rightButtons}>
                <Pressable
                    style={styles.iconButton}
                    onPress={() => router.push("/Parametres")}
                >
                    <MaterialIcons
                        name="settings"
                        size={28}
                        color="white"
                    />
                </Pressable>

                <Pressable
                    style={styles.iconButton}
                    onPress={handleLogout}
                >
                    <MaterialIcons
                        name="logout"
                        size={28}
                        color="white"
                    />
                </Pressable>
            </View>

            <StatsRow />
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        backgroundColor: "#9B4DFF",
        paddingTop: 48,
        paddingHorizontal: 20,
        paddingBottom: 24,
        borderBottomLeftRadius: 24,
        borderBottomRightRadius: 24,
    },

    left: {
        flexDirection: "row",
        alignItems: "center",
    },

    avatar: {
        width: 48,
        height: 48,
        borderRadius: 24,
        backgroundColor: "white",
        alignItems: "center",
        justifyContent: "center",
        marginRight: 12,
    },

    avatarText: {
        color: "#9B4DFF",
        fontSize: 20,
        fontWeight: "700",
    },

    name: {
        color: "white",
        fontSize: 16,
        fontWeight: "700",
    },

    role: {
        color: "white",
        fontSize: 13,
        fontWeight: "700",
    },

    rightButtons: {
        position: "absolute",
        top: 48,
        right: 20,
        flexDirection: "row",
    },

    iconButton: {
        marginLeft: 12,
    },
});