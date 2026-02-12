import { MaterialIcons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { useState } from "react";
import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

import Filtres from "../../components/Filtres";
import Footer from "../../components/Footer";
import HeaderProfile from "../../components/HeaderProfil";

const DEMANDES_DATA = [
    { id: "d1", nom: "Alice Dupont", type: "Quelqu'un me dérange", date: "10/01/2026 à 10h00", urgent: true },
    { id: "d2", nom: "Jean Martin", type: "Je suis perdu", date: "10/01/2026 à 15h00", urgent: false },
];

const AIDES_EN_COURS_DATA = [
    { id: "a1", nom: "Bob Dubois", type: "Mon transport n'est pas arrivé", date: "10/01/2026 à 11h30", urgent: false },
    { id: "a2", nom: "Cédric Pourier", type: "Je suis blessé(e)", date: "10/01/2026 à 14h00", urgent: true },
];

export default function HomeScreenHelper() {
    const router = useRouter();
    const [activeTab, setActiveTab] = useState<"demandes" | "aides">("demandes");
    const [activeFilter, setActiveFilter] = useState<"urgent" | "all" | "map">("all");

    const dataToShow = activeTab === "demandes" ? DEMANDES_DATA : AIDES_EN_COURS_DATA;

    const finalData = dataToShow.filter(item => {
        if (activeFilter === "urgent") return item.urgent === true;
        return true;
    });

    const urgentCount = dataToShow.filter(a => a.urgent).length;

    return (
        <SafeAreaView style={{ flex: 1, backgroundColor: "#fff" }}>
            <ScrollView contentContainerStyle={{ flexGrow: 1 }}>
                <HeaderProfile />

                <Filtres
                    active={activeFilter}
                    setActive={setActiveFilter}
                    urgentCount={urgentCount}
                />

                <View style={styles.contentContainer}>
                    <Text style={styles.mainTitle}>
                        {activeTab === "demandes" ? "Demandes reçues" : "Mes aides en cours"}
                    </Text>

                    {finalData.length > 0 ? (
                        finalData.map((item) => (
                            <View key={item.id} style={[styles.card, item.urgent && styles.urgentCardBorder]}>
                                {/* HEADER DE LA CARTE (Infos + Chat) */}
                                <View style={styles.cardHeader}>
                                    <View style={styles.leftSection}>
                                        <View style={styles.iconContainer}>
                                            <MaterialIcons name="help" size={30} color="#9f44ef" />
                                        </View>
                                        <View style={styles.infoContainer}>
                                            <View style={{ flexDirection: 'row', alignItems: 'center' }}>
                                                <Text style={styles.nom}>{item.nom}</Text>
                                                {item.urgent && <View style={styles.urgentBadge} />}
                                            </View>
                                            <Text style={styles.type}>Aide : {item.type}</Text>
                                            <Text style={styles.date}>{item.date}</Text>
                                        </View>
                                    </View>

                                    {activeTab === "aides" && (
                                        <Pressable onPress={() => router.push("/ChatScreen")}>
                                            <MaterialIcons
                                                name="chat-bubble"
                                                size={28}
                                                color={item.urgent ? "#EF4444" : "#9f44ef"}
                                            />
                                        </Pressable>
                                    )}
                                </View>

                                {/* BOUTONS D'ACTION (Accepter/Refuser) */}
                                {activeTab === "demandes" && (
                                    <View style={styles.actionRow}>
                                        <Pressable style={styles.btnDecline}>
                                            <Text style={styles.btnTextDecline}>Refuser</Text>
                                        </Pressable>

                                        <Pressable
                                            style={[
                                                styles.btnAccept,
                                                item.urgent && styles.btnAcceptUrgent
                                            ]}
                                            onPress={() => setActiveTab("aides")}
                                        >
                                            <Text style={styles.btnTextAccept}>Accepter</Text>
                                        </Pressable>
                                    </View>
                                )}
                            </View>
                        ))
                    ) : (
                        <View style={styles.emptyContainer}>
                            <Text style={styles.emptyText}>Rien à afficher ici</Text>
                        </View>
                    )}
                </View>
            </ScrollView>

            <View style={styles.footerWrapper}>
                <Footer onTabChange={(tab: any) => setActiveTab(tab)} />
            </View>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    contentContainer: {
        paddingHorizontal: 20,
        paddingTop: 10,
    },
    footerWrapper: {
        height: 80,
        backgroundColor: "#fff",
        borderTopWidth: 1,
        borderTopColor: "#eee",
    },
    emptyContainer: {
        flex: 1,
        marginTop: 50,
        alignItems: "center",
    },

    mainTitle: {
        fontSize: 26,
        fontWeight: "800",
        color: "#000",
        marginBottom: 20,
        lineHeight: 32,
    },
    emptyText: {
        color: "gray",
        fontSize: 16,
        fontWeight: "600",
    },

    card: {
        backgroundColor: "#FFF",
        borderRadius: 22,
        padding: 15,
        marginBottom: 15,
        borderWidth: 1,
        borderColor: "#9f44ef30",
        elevation: 4,
        shadowColor: "#000",
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.1,
        shadowRadius: 4,
    },
    cardHeader: {
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
    },
    urgentCardBorder: {
        borderColor: "#EF4444",
    },
    leftSection: {
        flexDirection: "row",
        alignItems: "center",
        flex: 1,
    },
    iconContainer: {
        width: 60,
        height: 60,
        borderRadius: 30,
        backgroundColor: "#F3E8FF",
        justifyContent: "center",
        alignItems: "center",
        borderWidth: 1,
        borderColor: "#E9D5FF",
    },
    infoContainer: {
        marginLeft: 15,
        flex: 1,
    },
    nom: {
        fontSize: 17,
        fontWeight: "700",
    },
    type: {
        fontSize: 14,
        color: "#444",
    },
    date: {
        fontSize: 12,
        color: "#999",
        marginTop: 4,
    },
    urgentBadge: {
        width: 8,
        height: 8,
        borderRadius: 4,
        backgroundColor: "#EF4444",
        marginLeft: 6,
    },

    // --- BOUTONS & ACTIONS ---
    actionRow: {
        flexDirection: "row",
        marginTop: 15,
        gap: 10,
    },
    btnAccept: {
        flex: 1,
        backgroundColor: "#9f44ef",
        paddingVertical: 10,
        borderRadius: 12,
        alignItems: "center",
    },
    btnAcceptUrgent: {
        backgroundColor: "#EF4444",
    },
    btnDecline: {
        flex: 1,
        backgroundColor: "#fff",
        paddingVertical: 10,
        borderRadius: 12,
        alignItems: "center",
        borderWidth: 1,
        borderColor: "#ddd",
    },
    btnTextAccept: {
        color: "#fff",
        fontWeight: "700",
    },
    btnTextDecline: {
        color: "#666",
        fontWeight: "700",
    },
});