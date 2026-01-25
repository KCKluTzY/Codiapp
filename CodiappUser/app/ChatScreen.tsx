import { ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import HeaderChat from "@/components/HeaderChat";
import Footer from "@/components/ChatFooter";

export default function ChatScreen() {
    return (
        <SafeAreaView style={{ flex: 1 }}>
            <ScrollView contentContainerStyle={{ flexGrow: 1 }}>
                <HeaderChat />
                <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
                    <Text style={{ color: "black", fontSize: 16, fontWeight: "600", textAlign: "center" }}>Conversation aidant/aidé</Text>
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
