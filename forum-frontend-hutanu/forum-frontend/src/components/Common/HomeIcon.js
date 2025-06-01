 import React from 'react';
import { Link } from 'react-router-dom';
import './HomeIcon.css';
import homepageIcon from '../../logos/homepage.png';

function HomeIcon() {
  return (
    <div className="home-icon">
      <Link to="/"><img src={homepageIcon} alt="Homepage" /></Link>
    </div>
  );
}

export default HomeIcon;
