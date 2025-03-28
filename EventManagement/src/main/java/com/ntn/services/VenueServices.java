/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.services;

import com.ntn.pojo.JdbcUtils;
import com.ntn.pojo.Venue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author NHAT
 */
public class VenueServices {
    
    public List<Venue> getVenues() throws SQLException {
        List<Venue> venues = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConnection()) {
            String sql = "SELECT*FROM venue";
            Statement stm = conn.createStatement();

            ResultSet rs = stm.executeQuery(sql);
            while (rs.next()) {
                Venue v = new Venue(rs.getInt("id"), rs.getString("name"), rs.getInt("capacity"));
                venues.add(v);
}
        }

        return venues;
    }

    public Venue getVenueById(int venueId) throws SQLException {
        Venue venue = null ;
        try (Connection conn = JdbcUtils.getConnection()) {
            String sql = "SELECT*FROM venue WHERE id = ?";
            PreparedStatement stm = conn.prepareCall(sql);

            stm.setInt(1, venueId);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) { // Chỉ cần kiểm tra 1 dòng
               venue = new Venue(rs.getInt("id"), rs.getString("name"), rs.getInt("capacity"));
            }
        }
        
        return venue;
    }
}
