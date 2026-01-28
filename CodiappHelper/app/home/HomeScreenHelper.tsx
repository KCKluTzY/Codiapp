import { ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Colors } from "../../constants/Colors";
import HeaderProfile from "../../components/HeaderProfil";
import Filtres from "../../components/Filtres";
import Footer from "../../components/Footer";
import { useRouter } from "expo-router";

export default function HomeScreenHelper() {
    const router = useRouter();
    return (
        <SafeAreaView style={{ flex: 1 }}>
            <ScrollView contentContainerStyle={{ flexGrow: 1 }}>
                <HeaderProfile />
                <Filtres />
                <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
                    <Text style={{ color: "black", fontSize: 16, fontWeight: "600", textAlign: "center" }}>Aucune demandes en cours</Text>
                </View>

            </ScrollView>

            <View style={{
                height: 80,
                backgroundColor: "#eee",
                justifyContent: "center",
                alignItems: "center"
            }}>
                <Footer />
            </View>
        </SafeAreaView >
    );
}
