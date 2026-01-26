import { StyleSheet, Text, View, Image, Pressable, TextInput } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Colors } from "../constants/Colors";
import { useRouter } from "expo-router";
import Ionicons from "@expo/vector-icons/build/Ionicons";

export default function Authentification() {
    const router = useRouter();

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
                        <Text style={styles.cardSubTitle}>
                            Entrez vos identifiants
                        </Text>
                    </View>
                </View>

                <TextInput
                    placeholder="Email"
                    placeholderTextColor={styles.cardSubTitle.color}
                    keyboardType="email-address"
                    style={styles.input}
                />

                <TextInput
                    placeholder="Mot de passe"
                    placeholderTextColor={styles.cardSubTitle.color}
                    secureTextEntry
                    style={styles.input}
                />

                <Pressable style={styles.button} onPress={() => router.push("/home/HomeScreenUser")}>
                    <Text style={styles.cardTitle}>Se connecter</Text>
                </Pressable>
            </View>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    safe: {
        flex: 1,
        backgroundColor: Colors.background,
    },
    header: {
        paddingHorizontal: 16,
        paddingTop: 8,
        paddingBottom: 12,
        backgroundColor: Colors.primary,
    },
    title: {
        marginTop: 18,
        fontSize: 26,
        fontWeight: "800",
        textAlign: "center",
        color: "#fff",
    },
    content: {
        flex: 1,
        padding: 24,
    },
    card: {
        backgroundColor: Colors.primary,
        borderRadius: 24,
        padding: 15,
        borderColor: Colors.primary_light,
        borderWidth: 5,
        flexDirection: "row",
        alignItems: "center",
        marginBottom: 32,
    },
    logo: {
        width: 60,
        height: 60,
        marginRight: 12,
    },
    textContainer: {
        flex: 1,
    },
    cardTitle: {
        fontSize: 18,
        fontWeight: "600",
        color: "#000",
    },
    cardSubTitle: {
        fontSize: 14,
        color: "#333",
        marginTop: 2,
    },
    input: {
        backgroundColor: "#fff",
        borderRadius: 16,
        paddingHorizontal: 16,
        paddingVertical: 14,
        fontSize: 16,
        marginBottom: 16,
        borderColor: Colors.primary_light,
        borderWidth: 2,
    },
    button: {
        backgroundColor: Colors.primary,
        borderRadius: 20,
        paddingVertical: 14,
        alignItems: "center",
        borderColor: Colors.primary_light,
        borderWidth: 3,
        marginTop: 8,
    },
    buttonText: {
        fontSize: 18,
        fontWeight: "700",
        color: "#fff",
    },
});