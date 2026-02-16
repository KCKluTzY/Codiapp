import { ScrollView, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import Footer from "../components/ChatFooter";
import HeaderChat from "../components/HeaderChat";

export default function ChatScreen() {
    return (
        <SafeAreaView style={{ flex: 1 }}>
            <ScrollView contentContainerStyle={{ flexGrow: 1 }}>
                {/* Header + placeholder conversation (API non intégrée) */}
                <HeaderChat />
                <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
                    <Text style={{ color: "black", fontSize: 16, fontWeight: "600", textAlign: "center" }}>Conversation (en attente d'API)</Text>
                </View>
            </ScrollView>

            <View style={{
                height: 80,
                backgroundColor: "#eee",
                justifyContent: "center",
                alignItems: "center"
            }}>
                {/* Footer : zone d'envoi (composant séparé) */}
                <Footer />
            </View>
        </SafeAreaView >
    );
}
